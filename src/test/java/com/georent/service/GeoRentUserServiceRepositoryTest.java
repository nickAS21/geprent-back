package com.georent.service;

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

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GeoRentUserServiceRepositoryTest {

    private static final Lot TEST_LOT = ServiceTestUtils.createTestLot();
    private static final LotDTO TEST_LOT_DTO = ServiceTestUtils.createTestLotDTO();
    private static final GeoRentUser TEST_USER = ServiceTestUtils.createTestUser();

    GeoRentUserService userService;

    private GeoRentUserRepository mockUserRepository = mock(GeoRentUserRepository.class);
    private LotRepository mockLotRepository = mock(LotRepository.class);
    private CoordinatesRepository mockCordinatesRepository = mock(CoordinatesRepository.class);
    private DescriptionRepository mockDescriptionRepository = mock(DescriptionRepository.class);
    private AWSS3Service mockDAWSS3Service = mock(AWSS3Service.class);

    private PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private String email = "mkyong@gmail23.com.aa";
    private String passPrincipal = "$2a$10$2O/w2twGJFNoLcnlOyJp0..IeZ2Wn3JXNts2wC62FT/TgTlQ9oqO6";
    GeoRentUser user = TEST_USER;
    GeoRentUser userPrincipal = TEST_USER;
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
                mockDescriptionRepository,
                mockDAWSS3Service
        );
        userPrincipal.setPassword(passPrincipal);

        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userPrincipal));
    }

    @Test
    public void WhenGetUserByEmail_Return_GeoRentUser() {
        // given
        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(TEST_USER));
        // when
        GeoRentUser geoRentUserOut = userService.getUserByEmail(email).get();
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        // then
        Assert.assertEquals(TEST_USER, geoRentUserOut);
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
        // given
        final GeoRentUserInfoDto expectedUserInfoDto = ServiceTestUtils.createTestUserInfoDTO();
        // when
        GeoRentUserInfoDto actualUserInfoDTO = userService.getUserInfo(principal);
        // then
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        Assert.assertEquals(expectedUserInfoDto, actualUserInfoDTO);
    }

    @Test
    public void WhenUpdateUser_Return_responseDTO_WithGeoRentUserUpdateDto() {
        // given
        final GeoRentUserUpdateDto testUserUpdateDto = ServiceTestUtils.createUserUpdateDto();
        when(mockUserRepository.save(any(GeoRentUser.class))).thenReturn(userPrincipal);
        // when
        GenericResponseDTO<GeoRentUserUpdateDto> responseDTO = userService.updateUser(principal, testUserUpdateDto);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockUserRepository, times(1)).save(any(GeoRentUser.class));
        // then
        Assert.assertEquals(Message.SUCCESS_UPDATE_USER.getDescription(), responseDTO.getMessage());
        Assert.assertEquals(testUserUpdateDto, responseDTO.getBody());
    }

    @Test
    public void WhengetUserLots_Return_List_LotDto() {
        // given
        List<Lot> lotsIn = Arrays.asList(TEST_LOT);
        when(mockLotRepository.findAllByGeoRentUser_Id(any(Long.class))).thenReturn(lotsIn);
        List<LotDTO> expectedLotsDTO = Arrays.asList(TEST_LOT_DTO);
        // when
        List<LotDTO> actualLotsDTO =  userService.getUserLots(principal);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1)).findAllByGeoRentUser_Id(any(Long.class));
        // then
        Assert.assertEquals(expectedLotsDTO, actualLotsDTO);
    }

    @Test
    public void WhenGetUserLotId_Return_LotDto() {
        // given
        when(mockLotRepository.findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class))).thenReturn(Optional.of(TEST_LOT));
        // when
        LotDTO lotDTOOut = userService.getUserLotId(principal, TEST_LOT.getId());
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1)).findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class));
        // then
        Assert.assertEquals(TEST_LOT_DTO, lotDTOOut);
    }

    @Test
    public void WhenSaveUserLot_Return_LotDto() {
        // given
        RegistrationLotDto registrationLotDto = ServiceTestUtils.registrationLotDto (TEST_LOT);
        when(mockLotRepository.save(any(Lot.class))).thenReturn(TEST_LOT);
        // when
        GenericResponseDTO responseDTO = userService.saveUserLot (principal, registrationLotDto);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1)).save(any(Lot.class));
        // then
        Assert.assertEquals(Message.SUCCESS_SAVE_LOT.getDescription(), responseDTO.getMessage());
        Assert.assertEquals(TEST_LOT_DTO, responseDTO.getBody());
    }

    @Test
    public void WhenDeleteUser_DeleteUserLots_DeleteUser_Return_MsgMessage_SUCCESS_DELETE_USER() {
        
        GenericResponseDTO responseDTO =  userService.deleteUser(principal);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        Assert.assertEquals(Message.SUCCESS_DELETE_USER.getDescription(), responseDTO.getMessage());
        Assert.assertNull(responseDTO.getBody());
    }

    @Test
    public void WhenDeleteteUserLotId_findByIdAndGeoRentUser_Id_Ok_Return_MsgMessage_SUCCESS_DELETE_LOT() {
        // given
        when(mockLotRepository.findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(TEST_LOT));
        // when
        GenericResponseDTO responseDTO =  userService.deleteteUserLotId (principal, TEST_LOT.getId());
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        verify(mockLotRepository, times(1))
                .findByIdAndGeoRentUser_Id(any(Long.class), any(Long.class));
        // then
        Assert.assertEquals(Message.SUCCESS_DELETE_LOT.getDescription(), responseDTO.getMessage());
        Assert.assertNull(responseDTO.getBody());
    }

    @Test
    public void WhenDeleteteUserLotAll_Return_MsgMessage_SUCCESS_DELETE_LOT() {
        GenericResponseDTO responseDTO =  userService.deleteteUserLotAll (principal);
        verify(mockUserRepository, times(1)).findByEmail(any(String.class));
        Assert.assertEquals(Message.SUCCESS_DELETE_LOTS.getDescription(), responseDTO.getMessage());
        Assert.assertNull(responseDTO.getBody());
    }
}

