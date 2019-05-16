package com.georent.service;

import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.GeoRentUser;
import com.georent.domain.Lot;
import com.georent.dto.*;
import com.georent.message.Message;
import com.georent.repository.CoordinatesRepository;
import com.georent.repository.DescriptionRepository;
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
    private final transient LotRepository lotRepository;
    private final transient CoordinatesRepository coordinatesRepository;
    private final transient DescriptionRepository descriptionRepository;

    @Autowired
    public GeoRentUserService(final GeoRentUserRepository userRepository,
                              final PasswordEncoder passwordEncoder,
                              final LotRepository lotRepository,
                              final CoordinatesRepository coordinatesRepository,
                              final DescriptionRepository descriptionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.lotRepository = lotRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.descriptionRepository = descriptionRepository;
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

    public GeoRentUserInfoDto getUserInfo(Principal principal){
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        return mapToUserInfoDTO(geoRentUser);
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

    public GenericResponseDTO saveUserLot(Principal principal, final RegistrationLotDto registrationLotDto){
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(registrationLotDto.getLatitude());
        coordinates.setLongitude(registrationLotDto.getLongitude());
        coordinates.setAddress(registrationLotDto.getAddress());

        Description description = new Description();
        description.setItemName(registrationLotDto.getItemName());
        description.setLotDescription(registrationLotDto.getLotDescription());
//        description.setPictureId(registrationLotDto.getItemPath());
        description.setPictureId(1L);

        Lot lot = new Lot();
        lot.setGeoRentUser(geoRentUser);
        lot.setCoordinates(coordinates);
        lot.setDescription(description);
        lotRepository.save(lot);
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_SAVE_LOT.getDescription());
        responseDTO.setBody(mapToLotDTO(lot));
        return responseDTO;
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
            coordinatesDTO.setAddress(coordinates.getAddress());

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

    private GeoRentUserInfoDto mapToUserInfoDTO(GeoRentUser geoRentUser){
        GeoRentUserInfoDto geoRentUserInfoDto = new GeoRentUserInfoDto();
        geoRentUserInfoDto.setId(geoRentUser.getId());
        geoRentUserInfoDto.setEmail(geoRentUser.getEmail());
        geoRentUserInfoDto.setFirstName(geoRentUser.getFirstName());
        geoRentUserInfoDto.setLastName(geoRentUser.getLastName());
        geoRentUserInfoDto.setPhoneNumber(geoRentUser.getPhoneNumber());
        geoRentUserInfoDto.setPassword(geoRentUser.getPassword());
        return geoRentUserInfoDto;
    }
}
