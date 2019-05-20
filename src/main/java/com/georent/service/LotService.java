package com.georent.service;

import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.Lot;
import com.georent.dto.CoordinatesDTO;
import com.georent.dto.DescriptionDTO;
import com.georent.dto.LotDTO;
import com.georent.repository.LotRepository;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LotService {

    private final transient LotRepository lotRepository;

    @Autowired
    public LotService(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    public List<LotDTO> getLotsDto() {
        return lotRepository.findAll()
                .stream()
                .map(this::mapToShortLotDTO)
                .collect(Collectors.toList());
    }

    public LotDTO getLotDto(Long id) {
        Lot lot = lotRepository.findById(id).orElseThrow(RuntimeException::new);
        return mapToLotDTO(lot);
    }

    private LotDTO mapToShortLotDTO(Lot lot) {
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

    private LotDTO mapToLotDTO(Lot lot) {
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
