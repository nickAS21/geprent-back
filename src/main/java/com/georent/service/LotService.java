package com.georent.service;

import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.Lot;
import com.georent.dto.CoordinatesDTO;
import com.georent.dto.DescriptionDTO;
import com.georent.dto.LotDTO;
import com.georent.dto.LotPageDTO;
import com.georent.dto.LotPageable;
import com.georent.dto.LotShortDTO;
import com.georent.dto.MethodPage;
import com.georent.exception.LotNotFoundException;
import com.georent.message.Message;
import com.georent.repository.LotRepository;
import com.google.common.collect.Iterators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LotService {

    private final transient LotRepository lotRepository;
    private final transient AWSS3Service awss3Service;

    @Autowired
    public LotService(LotRepository lotRepository,
                      final AWSS3Service awss3Service) {
        this.lotRepository = lotRepository;
        this.awss3Service = awss3Service;
    }

    /**
     * Reads the list of all lots of all users from the database,
     * and transforms them to the list of dto objects, using short form mapping.
     * Only lot id, coordinates and item name are set.
     *
     * @return the list of coordinates of all lots from the database.
     */
    public List<LotShortDTO> getLotsDto() {
        return lotRepository.findAll()
                .stream()
                .map(this::mapToLotShortDTO)
                .collect(Collectors.toList());
    }

    /**
     * Reads the lot with provided id from the database, and transforms it to the dto object.
     * If the lot was not found in the database, throws LotNotFoundException.
     *
     * @param id the id of the lot.
     * @return dto of the lot with provided id.
     */
    public LotDTO getLotDto(Long id) {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException(
                        Message.INVALID_GET_LOT_ID.getDescription() + " " + id)
                );
        return mapToLotDTO(lot);
    }

    /**
     * if numberPage > (PageLast) then metodPage = last;
     * if numberPage >= (PageLast) and metodPage == next then metodPage = last;
     * if numberPage > (PageLast+1) and metodPage == PREVOUS then metodPage = last;
     * if numberPage > (PageLast+1) and metodPage == PREVOUS_OR_FIRST then metodPage = last;
     * @param pageNumber
     * @param count
     * @param ids - result after search
     * totalElements - total count of lots
     * @return list of all lots one page in the format of List<LotPageDTO> with pageNumber  (LotPageable).
     */
    public LotPageable getPage(int pageNumber, int count, String methodPage, List<Long> ids) {
        Page <Lot>page;
        Pageable pageable = getPageable(pageNumber, count, methodPage, ids);
        if (ids != null) {
            page = lotRepository.findByIdIn(ids, pageable);
        }
        else {
            page = lotRepository.findAll(pageable);
        }
        pageNumber = page.getPageable().getPageNumber();
        int totalPages = page.getTotalPages();
        List<LotPageDTO> dtos = page.getContent()
                .stream()
                .map(this::mapToPageLotDTO)
                .collect(Collectors.toList());
        return new LotPageable(dtos, pageNumber + 1, totalPages);
    }

    private LotShortDTO mapToLotShortDTO(Lot lot) {
        Coordinates coordinates = lot.getCoordinates();
        CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
        coordinatesDTO.setLatitude(coordinates.getLatitude());
        coordinatesDTO.setLongitude(coordinates.getLongitude());
        coordinatesDTO.setAddress(coordinates.getAddress());

        LotShortDTO shortDto = new LotShortDTO();
        shortDto.setId(lot.getId());
        shortDto.setPrice(lot.getPrice());
        shortDto.setLotName(lot.getDescription().getLotName());
        shortDto.setCoordinates(coordinatesDTO);
        return shortDto;
    }

    private LotDTO mapToLotDTO(Lot lot) {
        Coordinates coordinates = lot.getCoordinates();
        Description description = lot.getDescription();
        Long id = lot.getId();

        CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
        coordinatesDTO.setLatitude(coordinates.getLatitude());
        coordinatesDTO.setLongitude(coordinates.getLongitude());
        coordinatesDTO.setAddress(coordinates.getAddress());

        DescriptionDTO descriptionDTO = new DescriptionDTO();
        descriptionDTO.setLotName(description.getLotName());
        descriptionDTO.setLotDescription(description.getLotDescription());
        Collections.copy(description.getPictureIds(), descriptionDTO.getPictureIds());
        LotDTO dto = new LotDTO();
        //  add URLs picture
        List<DeleteObjectsRequest.KeyVersion> keys = this.awss3Service.getKeysLot(lot.getId());
        for (DeleteObjectsRequest.KeyVersion keyFileName : keys) {
            URL url = this.awss3Service.generatePresignedURL(keyFileName.getKey());
            if (url != null) descriptionDTO.getURLs().add(url);
        }

        dto.setId(id);
        dto.setPrice(lot.getPrice());
        dto.setCoordinates(coordinatesDTO);
        dto.setDescription(descriptionDTO);
        return dto;
    }

    private LotPageDTO mapToPageLotDTO(Lot lot) {
        LotPageDTO dto = new LotPageDTO();
        dto.setId(lot.getId());
        dto.setPrice(lot.getPrice());
        dto.setAddress(lot.getCoordinates().getAddress());
        dto.setLotName(lot.getDescription().getLotName());
        List<DeleteObjectsRequest.KeyVersion> keys = this.awss3Service.getKeysLot(lot.getId());
        if (keys.size() > 0) {
            URL imageUrl = this.awss3Service.generatePresignedURL(keys.get(0).getKey());
            dto.setImageUrl(imageUrl);
        }
        return dto;
    }

    private Pageable getPageable(int pageNumber, int count, String methodPage, List<Long> ids) {
        long totalElements;
        MethodPage request = MethodPage.getType(methodPage);
        Pageable pageable = null;

        if (ids != null) {
            totalElements = ids.size();
        }
        else {
            totalElements = lotRepository.findAll().size();
        }
        int totalPages = (int) Math.ceil((float)totalElements/count);

        pageNumber = pageNumber < 0 ? 0 : pageNumber;
        switch (request) {
            case PREVIOUS:
            case PREVIOUS_OR_FIRST:
                pageNumber = (pageNumber - 1) > totalPages ? totalPages : pageNumber;
                request = pageNumber == totalPages ? MethodPage.getType("last") : request;
                break;
            case NEXT:
                pageNumber = (pageNumber + 1) > totalPages ? totalPages : pageNumber;
                request = pageNumber == totalPages ? MethodPage.getType("last") : request;
                break;
            case CURRENT:
                pageNumber = (pageNumber) > totalPages ? totalPages : pageNumber;
                break;
            case LAST:
                pageNumber = totalPages;
                break;
            default:
        }

        switch (request) {
            case FIRST:
                pageable = PageRequest.of(pageNumber, count).first();
                break;
            case NEXT:
                pageable = PageRequest.of(pageNumber, count).next();
                break;
            case PREVIOUS:
                pageable =  PageRequest.of(pageNumber, count).previous();
                break;
            case PREVIOUS_OR_FIRST:
                pageable = PageRequest.of(pageNumber, count).previousOrFirst();
                break;
            default:
                pageable =  PageRequest.of(pageNumber, count);
        }
        return pageable;
    }
}
