package com.georent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.GeoRentUser;
import com.georent.domain.Lot;
import com.georent.dto.GeoRentUserInfoDto;
import com.georent.dto.GeoRentUserUpdateDto;
import com.georent.dto.GenericResponseDTO;
import com.georent.dto.LotDTO;
import com.georent.dto.RegistrationLotDto;
import com.georent.dto.CoordinatesDTO;
import com.georent.dto.DescriptionDTO;
import com.georent.exception.LotNotFoundException;
import com.georent.message.Message;
import com.georent.repository.CoordinatesRepository;
import com.georent.repository.DescriptionRepository;
import com.georent.repository.GeoRentUserRepository;
import com.georent.repository.LotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;


@Service
public class GeoRentUserService {

    private final transient GeoRentUserRepository userRepository;
    private final transient PasswordEncoder passwordEncoder;
    private final transient LotRepository lotRepository;
    private final transient CoordinatesRepository coordinatesRepository;
    private final transient DescriptionRepository descriptionRepository;
    private final transient AWSS3Service awss3Service;

    @Autowired
    public GeoRentUserService(final GeoRentUserRepository userRepository,
                              final PasswordEncoder passwordEncoder,
                              final LotRepository lotRepository,
                              final CoordinatesRepository coordinatesRepository,
                              final DescriptionRepository descriptionRepository,
                              final AWSS3Service awss3Service) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.lotRepository = lotRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.descriptionRepository = descriptionRepository;
        this.awss3Service = awss3Service;
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

    /**
     * Reads user info from the database and maps it to GeoRentUserInfoDto object.
     * @param principal Current user identifier.
     * @return The user info in the format of GeoRentUserInfoDto.
     */
    public GeoRentUserInfoDto getUserInfo(Principal principal) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        return mapToUserInfoDTO(geoRentUser);
    }

    /**
     * Updates user info in the database.
     * @param principal Current user identifier.
     * @param geoRentUserUpdateDto Information to update.
     * @return The generic response, containing the proper message and incoming GeoRentUserUpdateDto object.
     */
    @Transactional
    public GenericResponseDTO<GeoRentUserUpdateDto> updateUser(Principal principal,
                                                               final GeoRentUserUpdateDto geoRentUserUpdateDto) {
        userRepository.save(mapFromUpdateUserDTO(principal, geoRentUserUpdateDto));
        GenericResponseDTO<GeoRentUserUpdateDto> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_UPDATE_USER.getDescription());
        responseDTO.setBody(geoRentUserUpdateDto);
        return responseDTO;
    }

    /**
     * Reads the list of user lots from the database and maps them to the LotDTO format.
     * @param principal Current user identifier.
     * @return The list of user lots in the LotDTO format.
     */
    public List<LotDTO> getUserLots(Principal principal) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        return lotRepository.findAllByGeoRentUser_Id(geoRentUser.getId())
                .stream()
                .map(this::mapToLotDTO)
                .collect(Collectors.toList());
    }

    /**
     * Reads the lot with specified id from the database and maps it to the LotDTO format.
     * @param principal Current user identifier.
     * @param id - The id of the specified lot.
     * @return The requested lot in the LotDTO format.
     */
    public LotDTO getUserLotId(Principal principal, long id) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        Lot lot = lotRepository.findByIdAndGeoRentUser_Id(id, geoRentUser.getId())
                .orElseThrow(() -> new LotNotFoundException(Message.INVALID_GET_LOT_ID.getDescription() + Long.toString(id)
                        + Message.INVALID_GET_LOT_ID_USER.getDescription(), geoRentUser.getId()));
        return mapToLotDTO(lot);
    }

    /**
     * Downloads the lot picture from pictures repository to the temp file.
     * @param principal Current user identifier.
     * @param id The id of the specified lot.
     * @return The lot with specified id in the format of LotDTO.
     */
    public LotDTO getUserLotIdUploadPicture(Principal principal, long id) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        Lot lot = lotRepository.findByIdAndGeoRentUser_Id(id, geoRentUser.getId())
                .orElseThrow(() -> new LotNotFoundException(Message.INVALID_GET_LOT_ID.getDescription() + Long.toString(id)
                        + Message.INVALID_GET_LOT_ID_USER.getDescription(), geoRentUser.getId()));
        String keyUrl = lot.getDescription().getItemName();
        String keyFile= keyUrl.substring(keyUrl.lastIndexOf("/")+1);
        try {
            Path filePath =  Files.createTempFile("tmp_", keyFile);
            awss3Service.getS3Object(keyFile, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapToLotDTO(lot);
    }


    /**
     * Saves the provided lot to the database.
     * @param principal Current user identifier.
     * @param registrationLotDto The lot to save in the registrationLotDto format.
     * @return The saved lot in the LotDTO format.
     */
    @Transactional
    public GenericResponseDTO<LotDTO> saveUserLot(Principal principal, final RegistrationLotDto registrationLotDto) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        Lot lot = lotRepository.save(mapRegistrationLotDtoToLot(registrationLotDto, geoRentUser));
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_SAVE_LOT.getDescription());
        responseDTO.setBody(mapToLotDTO(lot));
        return responseDTO;
    }

    /**
     picture for lot
     1) keyFileName = {userId}/{lotId}/{index in list picture}"
     index in list picture -> start == "0" если до отьезда не успею переделсть сущность Lot
     2) Перед записью - проверяем наличие по Path == {userId}/{lotId}/{index in list picture}/
     если есть - удаляем
     3) keyFileName (для fileUrl) нового храним в list pictureIds - >  in Description


     * @param multipartFiles
     * @param principal
     * @param registrationLotDtoStr
     * @return
     */
    @Transactional
    public ResponseEntity<?> saveUserLotUploadPicture(MultipartFile[] multipartFiles, Principal principal, String registrationLotDtoStr) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        ObjectMapper mapper = new ObjectMapper();
        RegistrationLotDto registrationLotDto = null;
        try {
            registrationLotDto = mapper.readValue(registrationLotDtoStr, RegistrationLotDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Message.INVALID_SAVE_LOT.getDescription() + " " + e.getMessage());
        }
        Lot lot = lotRepository.save(mapRegistrationLotDtoToLot(registrationLotDto, geoRentUser));
        for (MultipartFile multipartFile : multipartFiles) {
            int index = lot.getDescription().getPictureIds().size()+1;
            String keyFileName = Long.toString(geoRentUser.getId()) + "/" + Long.toString(lot.getId()) + "/" + Integer.toString(index);
            String keyFileNameS3 = this.awss3Service.uploadFile(multipartFile, keyFileName);
            if (!keyFileName.isEmpty()) {
                lot.getDescription().getPictureIds().add(Long.valueOf(index));

            }
        }
        lotRepository.save(lot);
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_SAVE_LOT.getDescription());
        responseDTO.setBody(mapToLotDTO(lot));
        return status(OK).body(responseDTO);
    }

    /**
     * Deletes the specified user and all its lots from the database.
     * @param principal Current user identifier.
     * @return Generic response, containing  the proper message.
     */
    @Transactional
    public GenericResponseDTO<LotDTO> deleteUser(Principal principal) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        lotRepository.deleteAllByGeoRentUser_Id(geoRentUser.getId());
        userRepository.delete(geoRentUser);
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_DELETE_USER.getDescription());
        return responseDTO;
    }


    /**
     * Deletes the user lot with the specified id.
     * @param principal Current user identifier.
     * @param id - The id of the lot to delete.
     * @return Generic response, containing the proper message.
     */
    @Transactional
    public GenericResponseDTO<LotDTO> deleteteUserLotId(Principal principal, long id) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        lotRepository.findByIdAndGeoRentUser_Id(id, geoRentUser.getId())
                .orElseThrow(() -> new LotNotFoundException(Message.INVALID_GET_LOT_ID + Long.toString(id) +
                        Message.INVALID_GET_LOT_ID_USER.getDescription(), geoRentUser.getId()));
        lotRepository.deleteById(id);
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_DELETE_LOT.getDescription());
        return responseDTO;
    }

    /**
     * Deletes all lots of the current user from the database.
     * @param principal Current user identifier.
     * @return Generic response, containing the proper message.
     */
    @Transactional
    public GenericResponseDTO<LotDTO> deleteteUserLotAll(Principal principal) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        lotRepository.deleteAllByGeoRentUser_Id(geoRentUser.getId());
        GenericResponseDTO<LotDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_DELETE_LOTS.getDescription());
        return responseDTO;
    }

    private GeoRentUser mapFromUpdateUserDTO(Principal principal, final GeoRentUserUpdateDto geoRentUserUpdateDto) {
        GeoRentUser geoRentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        geoRentUser.setLastName(geoRentUserUpdateDto.getLastName());
        geoRentUser.setFirstName(geoRentUserUpdateDto.getFirstName());
        geoRentUser.setPhoneNumber(geoRentUserUpdateDto.getPhoneNumber());
        return geoRentUser;
    }

    private LotDTO mapToLotDTO(Lot lot) {
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
                descriptionDTO.setPictureIds(new ArrayList<Long>(description.getPictureIds()));
                dto.setDescription(descriptionDTO);
            }
            return dto;
    }

    private GeoRentUserInfoDto mapToUserInfoDTO(GeoRentUser geoRentUser) {
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
//        Collections.copy(registrationLotDto.getPictureIds(), description.getPictureIds());
        lot.setDescription(description);
        return lot;
    }
}
