package com.georent.service;


import com.georent.controller.GeoRentUserController;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GeoRentUserServiceRepositoryTest {

    GeoRentUserService userService;

    private GeoRentUserController mockUserController = mock(GeoRentUserController.class);
    private GeoRentUserRepository mockUserRepository = mock(GeoRentUserRepository.class);
    private LotRepository mockLotRepository = mock(LotRepository.class);
    private CoordinatesRepository mockCordinatesRepository = mock(CoordinatesRepository.class);
    private DescriptionRepository mockDescriptionRepository = mock(DescriptionRepository.class);

    private MockMvc mockMvc;
    private PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private String email = "mkyong@gmail23.com.aa";
    private String passPrincipal = "$2a$10$2O/w2twGJFNoLcnlOyJp0..IeZ2Wn3JXNts2wC62FT/TgTlQ9oqO6";
    GeoRentUser user = getUser();
    GeoRentUser userPrincipal = getUser();;
    Principal principal = new Principal() {
        @Override
        public String getName() {
            return "";
        }
    };

    @Before
    public void setup() {
        userService = new GeoRentUserService(
                mockUserRepository,
                passwordEncoder,
                mockLotRepository,
                mockCordinatesRepository,
                mockDescriptionRepository
        );
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(mockUserController).build();
        userPrincipal.setPassword(passPrincipal);
    }

    @Test
    public void WhenGetUserByEmail_Return_GeoRentUser() {
        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(getUser()));
        GeoRentUser geoRentUserOut = userService.getUserByEmail(email).get();
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        Assert.assertEquals(getUser(), geoRentUserOut);
    }

    @Test
    public void WhenExistsUserByEmail_Ok_Return_True() {
        when(mockUserRepository.existsByEmail(any(String.class))).thenReturn(true);
        Assert.assertTrue(userService.existsUserByEmail(email));
        verify(mockUserRepository, times(1)).existsByEmail(any(String.class));
    }

    @Test
    public void WhenExistsUserByEmail_Err_Return_Fasle() {
        when(mockUserRepository.existsByEmail(any(String.class))).thenReturn(false);
        Assert.assertFalse(userService.existsUserByEmail(email));
        verify(mockUserRepository, times(1)).existsByEmail(any(String.class));
    }

    @Test
    public void WhenSaveNewUser_Save_User_With_EncodePassword() {
        when(passwordEncoder.encode(any(String.class))).thenReturn(passPrincipal);
        when(mockUserRepository.save(any(GeoRentUser.class))).thenReturn(user);
        GeoRentUser userOut = userService.saveNewUser(user);
        verify(mockUserRepository, times(1)).save(any(GeoRentUser.class));
        Assert.assertEquals(userOut, user);
    }

    @Test
    public void WhenGetUserInfo_Return_GeoRentUserInfoDtoWithPassPrincipal() {
        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userPrincipal));
        GeoRentUserInfoDto userOut = userService.getUserInfo(principal);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        Assert.assertEquals(userOut, userService.mapToUserInfoDTO(userPrincipal));
    }

    @Test
    public void WhenUpdateUser_Return_responseDTO_WithGeoRentUserUpdateDto() {
        GeoRentUserUpdateDto userUpdateDto = getUserUpdateDto();
        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userPrincipal));
        when(mockUserRepository.save(any(GeoRentUser.class))).thenReturn(userPrincipal);
        GenericResponseDTO<GeoRentUserUpdateDto> responseDTO = userService.updateUser(principal, userUpdateDto);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockUserRepository, times(1)).save(any(GeoRentUser.class));
        Assert.assertEquals(Message.SUCCESS_UPDATE_USER.getDescription(), responseDTO.getMessage());
        Assert.assertEquals(userUpdateDto, responseDTO.getBody());
    }

    @Test
    public void WhengetUserLots_Return_List_LotDto() {
        List<Lot> lotsIn = Arrays.asList(getLot ());
        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userPrincipal));
        when(mockLotRepository.findAllByGeoRentUser_Id(any(Long.class))).thenReturn(lotsIn);
        List<LotDTO> lotsDTOOut =  userService.getUserLots(principal);
        List<LotDTO> lotsDTOIn = lotsIn
                .stream()
                .map(this::mapToLotDTO)
                .collect(Collectors.toList());
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1)).findAllByGeoRentUser_Id(any(Long.class));
        Assert.assertEquals(lotsDTOIn, lotsDTOOut);
    }

    @Test
    public void WhenGetUserLotId_Return_LotDto() {
        Long lotId = 1L;
        Lot lotIn = getLot();
        LotDTO lotDTOIn = this.mapToLotDTO(lotIn);
        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userPrincipal));
        when(mockLotRepository.findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class))).thenReturn(Optional.of(lotIn));
        LotDTO lotDTOOut = userService.getUserLotId (principal, lotId);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1)).findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class));
        Assert.assertEquals(lotDTOIn, lotDTOOut);
    }

    @Test
    public void WhenSaveUserLot_Return_LotDto() {
        Lot lotIn = getLot();
        RegistrationLotDto registrationLotDto = registrationLotDto (lotIn);
        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userPrincipal));
        when(mockLotRepository.save(any(Lot.class))).thenReturn(lotIn);
        GenericResponseDTO responseDTO = userService.saveUserLot (principal, registrationLotDto);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1)).save(any(Lot.class));
        Assert.assertEquals(Message.SUCCESS_SAVE_LOT.getDescription(), responseDTO.getMessage());
        Assert.assertEquals(userService.mapToLotDTO(lotIn), responseDTO.getBody());
    }

    @Test
    public void WhenDeleteUser_DeleteUserLots_DeleteUser_Return_MsgMessage_SUCCESS_DELETE_USER() {
        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userPrincipal));
        GenericResponseDTO responseDTO =  userService.deleteUser(principal);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        Assert.assertEquals(Message.SUCCESS_DELETE_USER.getDescription(), responseDTO.getMessage());
        Assert.assertNull(responseDTO.getBody());
    }

    @Test
    public void WhenDeleteteUserLotId_findByIdAndGeoRentUser_Id_Ok_Return_MsgMessage_SUCCESS_DELETE_LOT() {
        Lot lotIn = getLot();
        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userPrincipal));
        when(mockLotRepository.findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class))).thenReturn(Optional.of(lotIn));
        GenericResponseDTO responseDTO =  userService.deleteteUserLotId (principal,lotIn.getId());
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1)).findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class));
        Assert.assertEquals(Message.SUCCESS_DELETE_LOT.getDescription(), responseDTO.getMessage());
        Assert.assertNull(responseDTO.getBody());
    }

    @Test
    public void WhenDeleteteUserLotAll_Return_MsgMessage_SUCCESS_DELETE_LOT() {
        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userPrincipal));
        GenericResponseDTO responseDTO =  userService.deleteteUserLotAll (principal);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        Assert.assertEquals(Message.SUCCESS_DELETE_LOTS.getDescription(), responseDTO.getMessage());
        Assert.assertNull(responseDTO.getBody());
    }

    private GeoRentUser getUser() {
        GeoRentUser user = new GeoRentUser();
        user.setId(1L);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail(email);
        user.setPassword("pass5678910");
        user.setPhoneNumber("123456789012");
        return user;
    }

    private GeoRentUserUpdateDto getUserUpdateDto() {
        GeoRentUser user = getUser();
        GeoRentUserUpdateDto userUpdateDto = new GeoRentUserUpdateDto();
        userUpdateDto.setFirstName(user.getFirstName());
        userUpdateDto.setLastName(user.getLastName());
        userUpdateDto.setPhoneNumber(user.getPhoneNumber());
        return userUpdateDto;
    }


    private Lot getLot () {
        GeoRentUser user = getUser();

        Coordinates coordinates = new Coordinates();
        coordinates.setId(1L);
        coordinates.setLongitude(801.800f);
        coordinates.setLatitude(901.900f);
        coordinates.setAddress("100 Киев 14");

        Description description = new Description();
        description.setId(1L);
        description.setPictureId(1L);
        description.setItemName("itemName2");
        description.setLotDescription("lotDescription2 lotDescription lotDescription");

        Lot lot = new Lot();
        lot.setId(1L);
        lot.setPrice(345L);
        lot.setGeoRentUser(user);
        lot.setCoordinates(coordinates);
        lot.setDescription(description);
        return lot;
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
            descriptionDTO.setPictureId(description.getPictureId());
            dto.setDescription(descriptionDTO);
        }
        return dto;
    }

    private RegistrationLotDto registrationLotDto (Lot lot){
        RegistrationLotDto registrationLotDto = new RegistrationLotDto();
        registrationLotDto.setPrice(lot.getPrice());
        registrationLotDto.setLongitude(lot.getCoordinates().getLongitude());
        registrationLotDto.setLatitude(lot.getCoordinates().getLatitude());
        registrationLotDto.setAddress(lot.getCoordinates().getAddress());
        //        description.setPictureId(registrationLotDto.getItemPath());
        registrationLotDto.setItemPath(Long.toString(lot.getDescription().getPictureId()));
        registrationLotDto.setItemName(lot.getDescription().getItemName());
        registrationLotDto.setLotDescription(lot.getDescription().getLotDescription());
        return registrationLotDto;
    }
}

