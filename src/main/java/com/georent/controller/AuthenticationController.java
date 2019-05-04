package com.georent.controller;

import com.georent.domain.GeoRentUser;
import com.georent.dto.AuthenticationResponseDTO;
import com.georent.dto.GenericResponseDTO;
import com.georent.dto.GeoRentUserDTO;
import com.georent.dto.LoginRequestDTO;
import com.georent.dto.RegistrationRequestDTO;
import com.georent.message.Message;
import com.georent.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/")
public class AuthenticationController {

    private final transient AuthenticationService authService;

    @Autowired
    public AuthenticationController(final AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> authenticateUser(@Valid @RequestBody final LoginRequestDTO authRequest,
                                                                      final HttpServletResponse response) {
        return status(OK).body(authService.loginUser(authRequest, response));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody final RegistrationRequestDTO signUpRequest) {
        GeoRentUser geoRentUser = authService.registerNewUserAccount(signUpRequest);
        GenericResponseDTO<GeoRentUserDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_REGISTRATION.getDescription());
        responseDTO.setBody(mapToGeoRentUserDTO(geoRentUser));
        return status(CREATED).body(responseDTO);
    }

    private GeoRentUserDTO mapToGeoRentUserDTO(GeoRentUser user){
        GeoRentUserDTO dto = new GeoRentUserDTO();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
    }

//    /**
//     * Initiating WebDataBinder for checking login form.
//     *
//     * @param binder new {@link DataBinder}
//     */
//    @InitBinder
//    public void initBinder(final WebDataBinder binder) {
//        binder.setValidator(new LoginRequestDTOValidator());
//    }

}
