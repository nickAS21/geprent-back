package com.georent.controller;


import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.Lot;
import com.georent.dto.CoordinatesDTO;
import com.georent.dto.DescriptionDTO;
import com.georent.dto.LotDTO;
import com.georent.repository.LotRepository;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("user")
public class LotController {

    private final LotRepository lotRepository;

    @Autowired
    public LotController(final LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    @GetMapping
    public ResponseEntity.BodyBuilder getAllLots() {
        List<LotDTO> allLots = lotRepository.findAll()
                .stream()
                .map(this::mapToLotDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok();
    }


    @GetMapping
    public ResponseEntity.BodyBuilder getAllLotsShortDescription() {
        List<LotDTO> allLots = lotRepository.findAll()
                .stream()
                .map(this::mapToShortLotDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok();
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
        dto.setPrice(Math.abs(RandomUtils.nextLong()));
        dto.setId(id);
        dto.setCoordinates(coordinatesDTO);
        dto.setDescription(descriptionDTO);

        return dto;

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
        dto.setDescription(descriptionDTO);

        return dto;

    }

}
