package com.georent.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GeoRentUserService {

    private final transient GeoRentUserRepository userRepository;
    private final transient PasswordEncoder passwordEncoder;
    private final LotRepository lotRepository;

    @Autowired
    public GeoRentUserService(final GeoRentUserRepository userRepository,
                              final PasswordEncoder passwordEncoder,
                              final LotRepository lotRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.lotRepository = lotRepository;
    }

    public Optional<GeoRentUser> getUserByEmail (final String email) {
        return userRepository.findByEmail(email);
    }

    public Boolean existsUserByEmail (final String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public GeoRentUser saveNewUser (final GeoRentUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<LotDTO> getUserLots(Principal principal){
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        return lotRepository.findAllByGeoRentUser_Id(geoRentUser.getId())
                .stream()
                .map(this::mapToLotDTO)
                .collect(Collectors.toList());
    }

    public LotDTO getUserLotId(Principal principal, long id){
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        Lot lot = lotRepository.findLotByUser_Id(geoRentUser.getId(), id);
        return mapToLotDTO(lot);
    }

    private LotDTO mapToLotDTO(Lot lot){
        if (lot == null) {
           return null;
        }
        else {
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


    private LotDTO mapToLotDTO(Optional<Lot> lot) {
        if (!lot.isEmpty()) {
            Coordinates coordinates = lot.get().getCoordinates();
            Description description = lot.get().getDescription();
            Long id = lot.get().getId();

            CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
            coordinatesDTO.setLatitude(coordinates.getLatitude());
            coordinatesDTO.setLongitude(coordinates.getLongitude());

            DescriptionDTO descriptionDTO = new DescriptionDTO();
            descriptionDTO.setItemName(description.getItemName());
            descriptionDTO.setLotDescription(description.getLotDescription());
            descriptionDTO.setPictureId(description.getPictureId());

            LotDTO dto = new LotDTO();
            dto.setId(id);
            dto.setCoordinates(coordinatesDTO);
            dto.setPrice(Math.abs(RandomUtils.nextLong()));
            dto.setDescription(descriptionDTO);
            return dto;
        }
        return null;
    }
}
