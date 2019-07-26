package com.georent.controller;

import com.georent.dto.ForgotEmailDto;
import com.georent.dto.ForgotUpdatePasswordDTO;
import com.georent.dto.LoginRequestDTO;
import com.georent.dto.RegistrationRequestDTO;
import com.georent.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;
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
//    @RequestMapping(
//            method = RequestMethod.POST,
//            value = "/login",
//            produces = "application/json"
//    )
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody final LoginRequestDTO authRequest,
                                                                      final HttpServletResponse response) {
        return status(OK).body(authService.loginUser(authRequest, response));
    }

    @PostMapping("/register")
//    @RequestMapping(
//            method = RequestMethod.POST,
//            value = "/register",
//            produces = "application/json"
//    )
    public ResponseEntity<?> registerUser(@Valid @RequestBody final RegistrationRequestDTO signUpRequest) {
        return status(CREATED).body(authService.registerUser(signUpRequest));
    }

    /**
     * Start FORGOT PASSWORD requests to endpoint "/forgotpassword" input param email
     * @param forgotEmailDto
     * @param request
     * @return
     */
    @PostMapping(value = "/forgotpassword")
    public ResponseEntity<?>  forgot (@Valid @RequestBody ForgotEmailDto forgotEmailDto,
                                      final HttpServletRequest request) {
        return authService.forgotPasswordUser(forgotEmailDto.getEmail(), forgotEmailDto.getApi(), request);
    }

    /**
     * Processes FORGOT PASSWORD requests to endpoint "/forgotpassword/save"
     * @param forgotUpdatePasswordDTO
     * @param request
     * @return
     */
    @PostMapping(value = "forgotpassword/save")
    public ResponseEntity<?> forgotSave(@Valid @RequestBody ForgotUpdatePasswordDTO forgotUpdatePasswordDTO,
                                        HttpServletRequest request) {
        return authService.forgotPasswordSave(forgotUpdatePasswordDTO.getPassword(), request);
    }
}
