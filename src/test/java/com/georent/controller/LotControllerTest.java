package com.georent.controller;

import com.georent.GeoRentStarter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GeoRentStarter.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LotControllerTest {

    @Autowired
    private LotController controllerToTest;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        controllerToTest = mock(LotController.class);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controllerToTest).build();
    }

    @Test
    public void getLotId_mapping_get_lot_id_Return_Status_ok() throws Exception {
        mockMvc.perform(get("/lot/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getLotAll_mapping_get_lot_Return_Status_ok() throws Exception {
        mockMvc.perform(get("/lot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test

    public void getLotAll_mapping_get_lots_Return_Status_isNotFound() throws Exception {
        mockMvc.perform(get("/lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }
}

