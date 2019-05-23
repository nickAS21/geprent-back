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

    public Optional<GeoRentUser> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public Boolean existsUserByEmail(final String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public GeoRentUser saveNewUser(final GeoRentUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public GeoRentUserInfoDto getUserInfo(Principal principal) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        return mapToUserInfoDTO(geoRentUser);
    }

    @Transactional
    public GenericResponseDTO updateUser(Principal principal, final GeoRentUserUpdateDto geoRentUserUpdateDto) {
        userRepository.save(mapFromUpdateUserDTO(principal, geoRentUserUpdateDto));
        GenericResponseDTO<GeoRentUserUpdateDto> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_UPDATE_USER.getDescription());
        responseDTO.setBody(geoRentUserUpdateDto);
        return responseDTO;
    }

    public List<LotDTO> getUserLots(Principal principal) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        return lotRepository.findAllByGeoRentUser_Id(geoRentUser.getId())
                .stream()
                .map(this::mapToLotDTO)
                .collect(Collectors.toList());
    }

    public LotDTO getUserLotId(Principal principal, long id) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        Lot lot = lotRepository.findByIdAndGeoRentUser_Id(id, geoRentUser.getId()).orElseThrow(RuntimeException::new);
        return mapToLotDTO(lot);
    }


    @Transactional
    public GenericResponseDTO saveUserLot(Principal principal, final RegistrationLotDto registrationLotDto) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        Lot lot = lotRepository.save(mapRegistrationLotDtoToLot(registrationLotDto, geoRentUser));
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_SAVE_LOT.getDescription());
        responseDTO.setBody(mapToLotDTO(lot));
        return responseDTO;
    }

    @Transactional
    public GenericResponseDTO saveUserLotUpload() {
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_SAVE_LOT.getDescription());
//        responseDTO.setBody(mapToLotDTO(lot));
        return responseDTO;
    }

    @Transactional
    public GenericResponseDTO deleteUser(Principal principal) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        lotRepository.deleteAllByGeoRentUser_Id(geoRentUser.getId());
        userRepository.delete(geoRentUser);
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_DELETE_USER.getDescription());
        return responseDTO;
    }


    @Transactional
    public GenericResponseDTO deleteteUserLotId(Principal principal, long id) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        lotRepository.findByIdAndGeoRentUser_Id(id, geoRentUser.getId()).orElseThrow(RuntimeException::new);
        lotRepository.deleteById(id);
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_DELETE_LOT.getDescription());
        return responseDTO;
    }

    @Transactional
    public GenericResponseDTO deleteteUserLotAll(Principal principal) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        lotRepository.deleteAllByGeoRentUser_Id(geoRentUser.getId());
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_DELETE_LOTS.getDescription());
        return responseDTO;
    }

    private GeoRentUser mapFromUpdateUserDTO(Principal principal, final GeoRentUserUpdateDto geoRentUserUpdateDto) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
        geoRentUser.setLastName(geoRentUserUpdateDto.getLastName());
        geoRentUser.setFirstName(geoRentUserUpdateDto.getFirstName());
        geoRentUser.setPhoneNumber(geoRentUserUpdateDto.getPhoneNumber());
        return geoRentUser;
    }

    public LotDTO mapToLotDTO(Lot lot) {
            LotDTO dto = new LotDTO();
            Long id = lot.getId();
            dto.setId(id);
            dto.setPrice(lot.getPrice());
            if (lot.getCoordinates() != null) {
                Coordinates coordinates = lot.getCoordinates();
                CoordinatesDTO coordinatesDTO = new CoordinatesDTO();
                coordinatesDTO.setLatitude(coordinates.getLatitude());
                coordinatesDTO.setLongitude(coordinates.getLongitude());
                coordinatesDTO.setAddress(coordinates.getAddress());
                dto.setCoordinates(coordinatesDTO);
            }
            if (lot.getDescription() != null) {
                Description description = lot.getDescription();
                DescriptionDTO descriptionDTO = new DescriptionDTO();
                descriptionDTO.setItemName(description.getItemName());
                descriptionDTO.setLotDescription(description.getLotDescription());
                descriptionDTO.setPictureId(description.getPictureId());
                dto.setDescription(descriptionDTO);
            }
            return dto;
    }

    public GeoRentUserInfoDto mapToUserInfoDTO(GeoRentUser geoRentUser) {
        GeoRentUserInfoDto geoRentUserInfoDto = new GeoRentUserInfoDto();
        geoRentUserInfoDto.setId(geoRentUser.getId());
        geoRentUserInfoDto.setEmail(geoRentUser.getEmail());
        geoRentUserInfoDto.setFirstName(geoRentUser.getFirstName());
        geoRentUserInfoDto.setLastName(geoRentUser.getLastName());
        geoRentUserInfoDto.setPhoneNumber(geoRentUser.getPhoneNumber());
        return geoRentUserInfoDto;
    }

    private Lot mapRegistrationLotDtoToLot(RegistrationLotDto registrationLotDto, GeoRentUser geoRentUser) {
        Lot lot = new Lot();
        lot.setPrice(registrationLotDto.getPrice());
        lot.setGeoRentUser(geoRentUser);
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(registrationLotDto.getLatitude());
        coordinates.setLongitude(registrationLotDto.getLongitude());
        coordinates.setAddress(registrationLotDto.getAddress());
        lot.setCoordinates(coordinates);
        Description description = new Description();
        description.setItemName(registrationLotDto.getItemName());
        description.setLotDescription(registrationLotDto.getLotDescription());
//        description.setPictureId(registrationLotDto.getItemPath());
        description.setPictureId(1L);
        lot.setDescription(description);
        return lot;
    }
}
