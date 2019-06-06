package com.georent.service;

import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.Lot;
import com.georent.dto.CoordinatesDTO;
import com.georent.dto.DescriptionDTO;
import com.georent.dto.LotDTO;
import com.georent.exception.LotNotFoundException;
import com.georent.message.Message;
import com.georent.repository.LotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LotService {

    private final transient LotRepository lotRepository;

    @Autowired
    public LotService(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    /**
     * Reads the list of all lots of all users from the database,
     * and transform them to list of short dto objects, i.e. list of coordinates.
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

    LotDTO mapToShortLotDTO(Lot lot) {
        Coordinates coordinates = lot.getCoordinates();
        Description description = lot.getDescription();
        Long id = lot.getId();

        CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
        coordinatesDTO.setLatitude(coordinates.getLatitude());
        coordinatesDTO.setLongitude(coordinates.getLongitude());

        DescriptionDTO descriptionDTO = new DescriptionDTO();
        descriptionDTO.setItemName(description.getItemName());

        LotDTO dto = new LotDTO();
        dto.setId(id);
        dto.setCoordinates(coordinatesDTO);
        dto.setDescription(descriptionDTO);
        return dto;
    }

    LotDTO mapToLotDTO(Lot lot) {
        Coordinates coordinates = lot.getCoordinates();
        Description description = lot.getDescription();
        Long id = lot.getId();

        CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
        coordinatesDTO.setLatitude(coordinates.getLatitude());
        coordinatesDTO.setLongitude(coordinates.getLongitude());

        DescriptionDTO descriptionDTO = new DescriptionDTO();
        descriptionDTO.setItemName(description.getItemName());
        descriptionDTO.setLotDescription(description.getLotDescription());
        descriptionDTO.setPictureId(description.getPictureId());

        LotDTO dto = new LotDTO();
        dto.setId(id);
        dto.setPrice(lot.getPrice());
        dto.setCoordinates(coordinatesDTO);
        dto.setDescription(descriptionDTO);
        return dto;
    }

}
