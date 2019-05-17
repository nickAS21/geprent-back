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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GeoRentStarter.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GeoRentUserControllerTest {

    @Autowired
    private GeoRentUserController controllerToTest;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        controllerToTest = mock(GeoRentUserController.class);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controllerToTest).build();
    }

    @Test
    public void getUserInfo_mapping_get_user_Return_Status_ok () throws Exception {
        mockMvc.perform(get("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void updateUser_mapping_patch_user_Return_Status_isBadRequest () throws Exception {
        mockMvc.perform(patch("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void deletetUser_mapping_delete_user_Return_Status_ok () throws Exception {
        mockMvc.perform(delete("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getUserLots_mapping_get_user_lots_Return_Status_ok () throws Exception {
        mockMvc.perform(get("/user/lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getUserLotId_mapping_get_user_lot_id_Return_Status_ok () throws Exception {
        mockMvc.perform(get("/user/lot/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void setUserLot_mapping_post_user_lot_Return_Status_isBadRequest() throws Exception {
        mockMvc.perform(post("/user/lot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void deletetLotId_mapping_delete_user_lot_id_Return_Status_ok() throws Exception {
        mockMvc.perform(delete("/user/lot/8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void deletetLots_mapping_delete_user_lots_Return_Status_ok() throws Exception {
        mockMvc.perform(delete("/user/lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
