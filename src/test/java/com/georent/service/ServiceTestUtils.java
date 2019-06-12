package com.georent.service;

import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.GeoRentUser;
import com.georent.domain.Lot;
import com.georent.dto.CoordinatesDTO;
import com.georent.dto.DescriptionDTO;
import com.georent.dto.GeoRentUserDTO;
import com.georent.dto.GeoRentUserInfoDto;
import com.georent.dto.GeoRentUserUpdateDto;
import com.georent.dto.LotDTO;
import com.georent.dto.RegistrationLotDto;

public class ServiceTestUtils {
    public static GeoRentUser createTestUser() {
        GeoRentUser user = new GeoRentUser();
        user.setId(1L);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("mkyong@gmail23.com.aa");
        user.setPassword("pass5678910");
        user.setPhoneNumber("123456789012");
        return user;
    }

    public static Coordinates createTestCoordinates() {
        Coordinates coordinates = new Coordinates();
        coordinates.setId(1L);
        coordinates.setLongitude(801.800f);
        coordinates.setLatitude(901.900f);
        coordinates.setAddress("100 Киев 14");
        return coordinates;
    }

    public static Description createTestDescription() {
        Description description = new Description();
        description.setId(1L);
//        description.setPictureId(1L);
        description.setLotName("itemName2");
        description.setLotDescription("lotDescription2 lotDescription lotDescription");
        return description;
    }

    public static Lot createTestLot() {
        Lot lot = new Lot();
        lot.setId(1L);
        lot.setPrice(345L);
        lot.setGeoRentUser(createTestUser());
        lot.setCoordinates(createTestCoordinates());
        lot.setDescription(createTestDescription());
        return lot;
    }

    public static GeoRentUserDTO createTestUserDTO() {
        final GeoRentUser testUser = createTestUser();
        GeoRentUserDTO userDTO = new GeoRentUserDTO();

        userDTO.setFirstName(testUser.getFirstName());
        userDTO.setLastName(testUser.getLastName());
        userDTO.setEmail(testUser.getEmail());

        return userDTO;
    }

    public static CoordinatesDTO createTestCoordinatesDTO() {
        final Coordinates testCoord = createTestCoordinates();
        CoordinatesDTO coordDTO = new CoordinatesDTO();

        coordDTO.setLongitude(testCoord.getLongitude());
        coordDTO.setLatitude(testCoord.getLatitude());
        coordDTO.setAddress(testCoord.getAddress());

        return coordDTO;
    }

    public static DescriptionDTO createTestDescriptionDTO() {
        final Description description = createTestDescription();
        DescriptionDTO descDTO = new DescriptionDTO();

        descDTO.setLotName(description.getLotName());
        descDTO.setLotDescription(description.getLotDescription());
//        descDTO.setPictureId(description.getPictureId());

        return descDTO;
    }

    public static LotDTO createTestShortLotDTO() {
        final Coordinates testCoord = createTestCoordinates();
        final Description testDescr = createTestDescription();
        final Lot lot = createTestLot();

        CoordinatesDTO coordDTO = new CoordinatesDTO();
        coordDTO.setLatitude(testCoord.getLatitude());
        coordDTO.setLongitude(testCoord.getLongitude());

        DescriptionDTO descriptionDTO = new DescriptionDTO();
        descriptionDTO.setLotName(testDescr.getLotName());

        LotDTO lotDTO = new LotDTO();

        lotDTO.setId(lot.getId());
        lotDTO.setCoordinates(coordDTO);
        lotDTO.setDescription(descriptionDTO);

        return lotDTO;
    }

    public static LotDTO createTestLotDTO() {
        final Lot lot = createTestLot();
        LotDTO lotDTO = new LotDTO();

        lotDTO.setCoordinates(createTestCoordinatesDTO());
        lotDTO.setDescription(createTestDescriptionDTO());

        lotDTO.setId(lot.getId());
        lotDTO.setPrice(lot.getPrice());

        return lotDTO;
    }

    public static GeoRentUserUpdateDto createUserUpdateDto() {
        GeoRentUser user = createTestUser();
        GeoRentUserUpdateDto userUpdateDto = new GeoRentUserUpdateDto();
        userUpdateDto.setFirstName(user.getFirstName());
        userUpdateDto.setLastName(user.getLastName());
        userUpdateDto.setPhoneNumber(user.getPhoneNumber());
        return userUpdateDto;
    }

    public static GeoRentUserInfoDto createTestUserInfoDTO() {
        final GeoRentUser testUser = createTestUser();
        GeoRentUserInfoDto infoDto = new GeoRentUserInfoDto();
        infoDto.setId(testUser.getId());
        infoDto.setEmail(testUser.getEmail());
        infoDto.setFirstName(testUser.getFirstName());
        infoDto.setLastName(testUser.getLastName());
        infoDto.setPhoneNumber(testUser.getPhoneNumber());
        return infoDto;
    }

    public static RegistrationLotDto registrationLotDto (Lot lot){
        RegistrationLotDto registrationLotDto = new RegistrationLotDto();
        registrationLotDto.setPrice(lot.getPrice());
        registrationLotDto.setLongitude(lot.getCoordinates().getLongitude());
        registrationLotDto.setLatitude(lot.getCoordinates().getLatitude());
        registrationLotDto.setAddress(lot.getCoordinates().getAddress());
        registrationLotDto.setLotName(lot.getDescription().getLotName());
        registrationLotDto.setLotDescription(lot.getDescription().getLotDescription());
        return registrationLotDto;
    }
}