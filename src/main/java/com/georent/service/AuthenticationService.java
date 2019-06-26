package com.georent.service;

import com.georent.config.JwtConfigurationProperties;
import com.georent.domain.GeoRentUser;
import com.georent.domain.GeoRentUserDetails;
import com.georent.domain.UserRole;
import com.georent.dto.*;
import com.georent.exception.RegistrationSuchUserExistsException;
import com.georent.message.GeoRentIHttpStatus;
import com.georent.message.Message;
import com.georent.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Collections;

@Service
public class AuthenticationService {

    public static final String BEARER = "Bearer ";

    private final transient AuthenticationManager authManager;
    private final transient JwtProvider jwtProvider;
    private final transient JwtConfigurationProperties jwtProperties;
    private final transient GeoRentUserService userService;

    @Autowired
    public AuthenticationService(final AuthenticationManager authManager,
                                 final JwtProvider jwtProvider,
                                 final JwtConfigurationProperties jwtProperties,
                                 final GeoRentUserService userService) {
        this.authManager = authManager;
        this.jwtProvider = jwtProvider;
        this.jwtProperties = jwtProperties;
        this.userService = userService;
    }

    public AuthenticationResponseDTO loginUser(final LoginRequestDTO loginRequest,
                                               final HttpServletResponse response) {

            GeoRentUser geoRentUser = userService.getUserByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException(Message.USER_NOT_FOUND_ERROR.getDescription()));

                Authentication authentication = authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                GeoRentUserDetails userPrincipal = (GeoRentUserDetails) authentication.getPrincipal();
                String accessToken = jwtProvider.generateAccessToken(userPrincipal);
                response.setHeader(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
                return AuthenticationResponseDTO
                        .builder()
                        .accessToken(accessToken)
                        .tokenType(BEARER.trim())
                        .dateCreate(LocalDate.now())
                        .expiresIn(jwtProperties.getExpiresIn())
                        .role(Collections.singleton(UserRole.USER))
                        .build();

    }

    public GeoRentUser registerNewUserAccount(final RegistrationRequestDTO registerUserRequest) {
        if (userService.existsUserByEmail(registerUserRequest.getEmail())) {
            throw new RegistrationSuchUserExistsException(GeoRentIHttpStatus.REGISTRATION_USER_ERROR.getReasonPhrase());
        }

        GeoRentUser user = new GeoRentUser();
        user.setFirstName(registerUserRequest.getFirstName());
        user.setLastName(registerUserRequest.getLastName());
        user.setEmail(registerUserRequest.getEmail());
        user.setPassword(registerUserRequest.getPassword());
        user.setPhoneNumber(registerUserRequest.getPhoneNumber());
        return userService.saveNewUser(user);
    }

    public GenericResponseDTO registerUser(final RegistrationRequestDTO registerUserRequest) {
        GeoRentUser geoRentUser = registerNewUserAccount(registerUserRequest);
        GenericResponseDTO<GeoRentUserDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_REGISTRATION.getDescription());
        responseDTO.setBody(mapToGeoRentUserDTO(geoRentUser));
        return responseDTO;
    }


    public GeoRentUserDTO mapToGeoRentUserDTO(GeoRentUser user){
        GeoRentUserDTO dto = new GeoRentUserDTO();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
    }


}
