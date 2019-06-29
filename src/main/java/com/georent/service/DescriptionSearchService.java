package com.georent.service;

import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.dto.DescriptionDTO;
import com.georent.dto.LotPageDTO;
import com.georent.dto.LotPageable;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DescriptionSearchService {

    private final EntityManagerFactory entityManagerFactory;
    private final LotService lotService;
    private final AWSS3Service awss3Service;

    @Autowired
    public DescriptionSearchService(EntityManagerFactory entityManagerFactory,
                                    LotService lotService,
                                    AWSS3Service awss3Service) {
        this.entityManagerFactory = entityManagerFactory;
        this.lotService = lotService;
        this.awss3Service = awss3Service;
    }

    /**
     * Search all lots with filters: "query" on Fields: "lotName" and "lotDescription" in class Description
     *
     * @param searchTerm
     * @return all lots with filters: "query" on Fields: "lotName" and "lotDescription" in the format  List<DescriptionDTO>
     */
    public List<DescriptionDTO> fuzzyLotSearch(String searchTerm) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Session currentSession = sessionFactory.openSession();
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession);
        QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Description.class).get();
        Query query = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(2).withPrefixLength(10).onFields("lotName", "lotDescription")
                .matching(searchTerm).createQuery();
        javax.persistence.Query fullTextQuery = fullTextSession.createFullTextQuery(query, Description.class);
        List<Description> resultList = fullTextQuery.getResultList();
        return resultList
                .stream()
                .map(description -> {
                    DescriptionDTO dto = new DescriptionDTO();
                    dto.setLotDescription(description.getLotDescription());
                    dto.setLotName(description.getLotName());
                    dto.setPictureIds(description.getPictureIds());
                    dto.setURLs(Collections.emptyList());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Search allLots with filters: "address" to the field address in class Coordinates
     * Search allLots with filters: "lotname" to the field lotName in class Description
     * and/or: if param equals !isBlank, not filters to this param
     *
     * @param address
     * @param lotName
     * @return all lots with filters: "address" and "lotname" in the format  Set<LotPageDTO>
     */
    public Set<LotPageDTO> fuzzyLotNameAndAddressSearch(String address, String lotName) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Session currentSession = sessionFactory.openSession();
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession);

        Map<Long, LotPageDTO> hm = new HashMap<>();
        if (!StringUtils.isBlank(address)) {
            List<Coordinates> coordinates = getLotsSearchAdr(fullTextSession, address);
            hm = coordinates
                    .stream()
                    .map(this::mapCoordinatesDTO)
                    .collect(Collectors.toMap(LotPageDTO::getId, Function.identity()));
        }
        if (!StringUtils.isBlank(lotName)) {
            List<Description> descriptions = getLotsSearchLotName(fullTextSession, lotName);
            Map<Long, LotPageDTO> hmSrc = descriptions
                    .stream()
                    .map(this::mapDescriptionDTO)
                    .collect(Collectors.toMap(LotPageDTO::getId, Function.identity()));
            hm.putAll(hmSrc);
        }
        Set<LotPageDTO> valueSet = new HashSet<>(hm.values());
        return valueSet
                .stream()
                .sorted(Comparator.comparing(LotPageDTO::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }


    /**
     * Search allLots with filters: "address" to the field address in class Coordinates
     * Search allLots with filters: "lotname" to the field lotName in class Description
     * and/or: if param equals !isBlank, not filters to this param
     *
     * @param pageNumber
     * @param count
     * @param methodPage
     * @param address
     * @param lotName    List<Long> ids - result all lotId after search with filters: "address" and "lotname"
     * @return list of all lots one page in the format of List<LotPageDTO> with pageNumber  (LotPageable).
     */
    public LotPageable fuzzyLotPageNameAndAddressSearch(int pageNumber, int count, String methodPage, String address, String lotName) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Session currentSession = sessionFactory.openSession();
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession);

        Set<Long> idSet = new HashSet<>();
        if (!StringUtils.isBlank(address)) {
            List<Coordinates> coordinates = getLotsSearchAdr(fullTextSession, address);
            idSet = coordinates
                    .stream()
                    .map(Coordinates::getId)
                    .collect(Collectors.toSet());
        }

        if (!StringUtils.isBlank(lotName)) {
            List<Description> descriptions = getLotsSearchLotName(fullTextSession, lotName);
            Set<Long> src = descriptions
                    .stream()
                    .map(Description::getId)
                    .collect(Collectors.toSet());
            idSet.addAll(src);
        }
        List<Long> ids = new ArrayList<>(idSet);
        return this.lotService.getPage(pageNumber, count, methodPage, ids);
    }

    /**
     * @param fullTextSession
     * @param address
     * @return List<Coordinates> with filters: "address"
     */
    private List<Coordinates> getLotsSearchAdr(FullTextSession fullTextSession, String address) {

        QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Coordinates.class).get();
        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(2)
                .withPrefixLength(10)
                .onFields("address")
                .matching(address)
                .createQuery();
        javax.persistence.Query fullTextQuery = fullTextSession.createFullTextQuery(query, Coordinates.class);
        List<Coordinates> resultList = fullTextQuery.getResultList();
        return resultList;
    }

    /**
     * @param fullTextSession
     * @param lotName
     * @return List<Description> with filters: "lotName"
     */
    private List<Description> getLotsSearchLotName(FullTextSession fullTextSession, String lotName) {

        QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Description.class).get();

        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(2)
                .withPrefixLength(10)
                .onFields("lotName")
                .matching(lotName)
                .createQuery();
        javax.persistence.Query fullTextQuery = fullTextSession.createFullTextQuery(query, Description.class);
        List<Description> resultList = fullTextQuery.getResultList();
        return resultList;
    }

    private LotPageDTO mapCoordinatesDTO(Coordinates coordinates) {
        LotPageDTO dto = new LotPageDTO();
        dto.setId(coordinates.getLot().getId());
        dto.setPrice(coordinates.getLot().getPrice());
        dto.setAddress(coordinates.getAddress());
        dto.setLotName(coordinates.getLot().getDescription().getLotName());
        dto.setImageUrl(getUrl(dto.getId()));
        return dto;
    }

    private LotPageDTO mapDescriptionDTO(Description description) {
        LotPageDTO dto = new LotPageDTO();
        dto.setId(description.getLot().getId());
        dto.setPrice(description.getLot().getPrice());
        dto.setAddress(description.getLot().getCoordinates().getAddress());
        dto.setLotName(description.getLotName());
        dto.setImageUrl(getUrl(dto.getId()));
        return dto;
    }

    private URL getUrl(Long lotId) {
        List<DeleteObjectsRequest.KeyVersion> keys = this.awss3Service.getKeysLot(lotId);
        if (keys.size() > 0) {
            return this.awss3Service.generatePresignedURL(keys.get(0).getKey());

        }
        return null;
    }

}
