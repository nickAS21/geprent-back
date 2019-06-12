package com.georent.service;

import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.Lot;
import com.georent.dto.CoordinatesDTO;
import com.georent.dto.DescriptionDTO;
import com.georent.dto.LotDTO;
import com.georent.dto.LotPageDTO;
import com.georent.exception.LotNotFoundException;
import com.georent.message.Message;
import com.georent.repository.LotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Collections;
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
     * @return the list of coordinates of all lots from the database.
     */
    public List<LotDTO> getLotsDto() {
        return lotRepository.findAll()
                .stream()
                .map(this::mapToShortLotDTO)
                .collect(Collectors.toList());
    }

    /**
     * Reads the lot with provided id from the database, and transforms it to the dto object.
     * If the lot was not found in the database, throws LotNotFoundException.
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
     *
     * @param numberPage
     * @param count
     * @return List<LotDTO>
     */

    public List<LotPageDTO> getPage(int numberPage, int count) {
        Page<Lot> page = lotRepository.findAll(PageRequest.of(numberPage, count));
        List<Lot> lots = page.getContent();
        return lots.stream()
                .map(this::mapToPageLotDTO)
                .collect(Collectors.toList());
    }

    private LotDTO mapToShortLotDTO(Lot lot) {
        Coordinates coordinates = lot.getCoordinates();
        Description description = lot.getDescription();
        Long id = lot.getId();

        CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
        coordinatesDTO.setLatitude(coordinates.getLatitude());
        coordinatesDTO.setLongitude(coordinates.getLongitude());

        DescriptionDTO descriptionDTO = new DescriptionDTO();
        descriptionDTO.setLotName(description.getLotName());

        LotDTO dto = new LotDTO();
        dto.setId(id);
        dto.setCoordinates(coordinatesDTO);
        dto.setDescription(descriptionDTO);
        return dto;
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
        List<DeleteObjectsRequest.KeyVersion> keys =  this.awss3Service.getKeysLot(lot.getId());
        for (DeleteObjectsRequest.KeyVersion keyFileName: keys) {
            URL url = this.awss3Service.GeneratePresignedURL(keyFileName.getKey());
            if (url != null) dto.getDescription().getURLs().add(url);
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
        List<DeleteObjectsRequest.KeyVersion> keys =  this.awss3Service.getKeysLot(lot.getId());
        if (keys.size() > 0) {
            URL imageUrl = this.awss3Service.GeneratePresignedURL(keys.get(0).getKey());
            dto.setImageUrl(imageUrl);
        }
        return dto;
    }
}
