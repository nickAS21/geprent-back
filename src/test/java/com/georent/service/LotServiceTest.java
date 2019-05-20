package com.georent.service;

import com.georent.controller.LotController;
import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.GeoRentUser;
import com.georent.domain.Lot;
import com.georent.dto.LotDTO;
import com.georent.repository.LotRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class LotServiceTest {

    @MockBean
    LotService serviceToTest;

    @Autowired
    private LotController mockController;

    @Autowired
    private LotRepository mockRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockController = mock(LotController.class);
        mockRepository = mock(LotRepository.class);
        serviceToTest = new LotService(mockRepository);
        this.mockMvc = MockMvcBuilders.standaloneSetup(mockController).build();
    }

    @Test
    public void Wgen_Service_GetAllLots_Return_AllLotsDto() {
        List<Lot> lots = Arrays.asList(getLot ());
        when(mockRepository.findAll()).thenReturn(lots);
        List<LotDTO> lotDTOSOut = serviceToTest.getLotsDto();
        verify(mockRepository, times(1)).findAll();
        List<LotDTO> lotDTOSIn = Arrays.asList(serviceToTest.mapToShortLotDTO(getLot()));
        Assert.assertEquals(lotDTOSIn,lotDTOSOut);
    }

    @Test
    public void When_Service_getLotDto_Id_One_Return_LotDto_Id_One() {
        when(mockRepository.findById(any(Long.class))).thenReturn(Optional.of(getLot()));
        LotDTO lotDTO = serviceToTest.getLotDto(1L);
        verify(mockRepository, times(1)).findById(any(Long.class));
        Assert.assertEquals(lotDTO,serviceToTest.mapToLotDTO(getLot()));
    }

    private Lot getLot () {
        GeoRentUser user = new GeoRentUser();
        user.setId(1L);
        user.setFirstName("firstName");
        user.setFirstName("lastName");
        user.setEmail("mkyong@gmail23.com.aa");
        user.setPassword("pass5678910");
        user.setPhoneNumber("123456789012");

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
}
