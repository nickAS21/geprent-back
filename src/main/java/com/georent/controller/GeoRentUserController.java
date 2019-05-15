package com.georent.controller;

import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.GeoRentUser;
import com.georent.domain.Lot;
import com.georent.dto.CoordinatesDTO;
import com.georent.dto.DescriptionDTO;
import com.georent.dto.LotDTO;
import com.georent.repository.GeoRentUserRepository;
import com.georent.repository.LotRepository;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("user")
public class GeoRentUserController {

    private final GeoRentUserRepository userRepository;

    private final LotRepository lotRepository;

    @Autowired
    public GeoRentUserController(final GeoRentUserRepository userRepository,
                                 final LotRepository lotRepository) {
        this.userRepository = userRepository;
        this.lotRepository = lotRepository;
    }

    @GetMapping
    public ResponseEntity<String> getSecretResponse(Principal principal){
        String name = principal.getName();
        return ResponseEntity.ok(String.format("Hello %s.", name));
    }

    @GetMapping("lots")
    public ResponseEntity<?> getLots(Principal principal){
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        List<LotDTO> allByGeoRentUser_id = lotRepository.findAllByGeoRentUser_Id(geoRentUser.getId())
                .stream()
                .map(this::mapToLotDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(allByGeoRentUser_id);
    }

    private LotDTO mapToLotDTO(Lot lot){
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
}