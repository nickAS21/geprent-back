package com.georent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.georent.GeoRentStarter;
import com.georent.domain.GeoRentUser;
import com.georent.dto.LoginRequestDTO;
import com.georent.dto.RegistrationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GeoRentStarter.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {

    @Autowired
    private AuthenticationController controllerToTest;

    ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        controllerToTest = mock(AuthenticationController.class);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controllerToTest).build();
    }

    @Test
    public void authenticateUser_mapping_post_login_Return_Status_ok () throws Exception {
        LoginRequestDTO reqest = new LoginRequestDTO();
        reqest.setEmail("mkyong@gmail23.com.aa");
        reqest.setPassword( "pass5678910");
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void registerUser_mapping_post_register_Return_Status_ok () throws Exception {
        RegistrationRequestDTO reqest = new RegistrationRequestDTO();
        reqest.setEmail("mkyong@gmail23.com.aa");
        reqest.setPassword( "pass5678910");

        GeoRentUser user = new GeoRentUser();
        user.setEmail(reqest.getEmail());
        user.setFirstName(reqest.getFirstName());
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void registerUser_mapping_post_register_Return_Status_isBadRequest () throws Exception {
        RegistrationRequestDTO reqest = new RegistrationRequestDTO();
        reqest.setEmail("mkyong@gmail23.com.aa");

        GeoRentUser user = new GeoRentUser();
        user.setEmail(reqest.getEmail());
        user.setFirstName(reqest.getFirstName());
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
