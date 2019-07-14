package com.georent.controller;

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

    @PostMapping(value = "/forgotpassword/runbrowser")
    public ResponseEntity<?> forgotRunBrowser(HttpServletRequest request) {
        return authService.forgotRunBrowser(request);
    }

    @PostMapping(value = "/forgotpassword")
    public ResponseEntity<?>  forgot (@RequestParam(name = "login") String login,
                                      HttpServletRequest request) {
        return authService.forgotUser(login, request);
    }

    @PostMapping(value = "/forgotpassword/signup")
    public ResponseEntity<?> forgotSignUp(HttpServletRequest request) {
        return authService.forgotRunBrowser(request);
    }


    @PostMapping(value = "/forgotpassword/save")
    public ResponseEntity<?>  forgotSave (@RequestParam(name = "login") String login,
                                          @RequestParam(name = "message") String message,
                                      HttpServletRequest request) {
        return authService.forgotUser(login, request);
    }

}
