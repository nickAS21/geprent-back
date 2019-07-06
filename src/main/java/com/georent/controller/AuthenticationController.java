package com.georent.controller;

import com.georent.dto.AuthenticationResponseDTO;
import com.georent.dto.LoginForgotPasswordDTO;
import com.georent.dto.LoginRequestDTO;
import com.georent.dto.RegistrationRequestDTO;
import com.georent.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;

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

    @RequestMapping(value = "/forgotpassword")
    public ResponseEntity<?>  forgot (@Valid @RequestBody final LoginForgotPasswordDTO authRequest,
                                      HttpServletRequest request) {
        return authService.forgotUser(authRequest, request);
    }

}
