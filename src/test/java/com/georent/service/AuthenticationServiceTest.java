package com.georent.service;

import com.georent.config.JwtConfigurationProperties;
import com.georent.controller.AuthenticationController;
import com.georent.domain.GeoRentUser;
import com.georent.domain.GeoRentUserDetails;
import com.georent.dto.AuthenticationResponseDTO;
import com.georent.dto.GenericResponseDTO;
import com.georent.dto.LoginRequestDTO;
import com.georent.dto.RegistrationRequestDTO;
import com.georent.exception.UserRegistrationException;
import com.georent.message.Message;
import com.georent.security.JwtProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    private AuthenticationController mockAuthenticationController = mock(AuthenticationController.class);
    private AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
    private JwtProvider jwtProvider = mock(JwtProvider.class);
    private JwtConfigurationProperties jwtProperties = mock(JwtConfigurationProperties.class);
    private GeoRentUserService mockUserService = mock(GeoRentUserService.class);

    private MockMvc mockMvc;
    private String passPrincipal = "$2a$10$2O/w2twGJFNoLcnlOyJp0..IeZ2Wn3JXNts2wC62FT/TgTlQ9oqO6";

    @Before
    public void setup() {
        authenticationService = new AuthenticationService(
                this.mockAuthManager,
                this.jwtProvider,
                this.jwtProperties,
                this.mockUserService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(mockAuthenticationController).build();
    }

    @Test
    public void WhenRegisterUser_Ok_Return_GenericResponseDTO() {
        RegistrationRequestDTO registerUserRequest = getRegistrationRequestDTO();
        GeoRentUser user = mapFromRegDtoToUser(registerUserRequest);
        when(mockUserService.saveNewUser(any(GeoRentUser.class))).thenReturn(user);
        when(mockUserService.existsUserByEmail(any(String.class))).thenReturn(false);
        GenericResponseDTO responseDTO = authenticationService.registerUser(registerUserRequest);
        verify(mockUserService, times(1)).saveNewUser(any(GeoRentUser.class));
        verify(mockUserService, times(1)).existsUserByEmail(any(String.class));
        Assert.assertEquals(Message.SUCCESS_REGISTRATION.getDescription(), responseDTO.getMessage());
        Assert.assertEquals(authenticationService.mapToGeoRentUserDTO(user), responseDTO.getBody());
    }

    @Test
    public void WhenRegisterUser_Err_Return_UserRegistrationException() {
        RegistrationRequestDTO registerUserRequest = getRegistrationRequestDTO();
        UserRegistrationException userRegistrationException = Assertions.assertThrows(UserRegistrationException.class, () -> {
            when(mockUserService.existsUserByEmail(any(String.class))).thenReturn(true);
            authenticationService.registerNewUserAccount(registerUserRequest);
        });
        Assert.assertEquals(userRegistrationException.getMessage(), Message.REGISTRATION_USER_ERROR.getDescription());
    }

    @Test
    public void WhenLoginUser_Ok_Return_AuthenticationResponseDTO() {
        LoginRequestDTO loginRequest = getLoginRequestDTO();
        GeoRentUser geoRentUser = mapFromRegDtoToUser(getRegistrationRequestDTO());
        geoRentUser.setId(1L);
        geoRentUser.setPassword(passPrincipal);
        GeoRentUserDetails principal = GeoRentUserDetails.create(geoRentUser);
        HttpServletResponse response = getHttpServletResponse();
        Authentication authentication = getAuthentication(principal);

        when(jwtProperties.getExpiresIn()).thenReturn(3600000L);
        when(jwtProvider.generateAccessToken(any(GeoRentUserDetails.class))).thenReturn(passPrincipal);
        when(mockAuthManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()))).thenReturn(authentication);
        when(mockUserService.getUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(geoRentUser));
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.loginUser(loginRequest,
                response);
    }

    private RegistrationRequestDTO getRegistrationRequestDTO() {
        RegistrationRequestDTO registerUserRequest = new RegistrationRequestDTO();
        registerUserRequest.setFirstName("firstName");
        registerUserRequest.setLastName("lastName");
        registerUserRequest.setEmail("mkyong@gmail.com.aa");
        registerUserRequest.setPassword("pass5678910");
        registerUserRequest.setPhoneNumber("123456789012");
        return registerUserRequest;
    }

    private GeoRentUser mapFromRegDtoToUser(RegistrationRequestDTO registerUserRequest) {
        GeoRentUser user = new GeoRentUser();
        user.setId(1L);
        user.setFirstName(registerUserRequest.getFirstName());
        user.setLastName(registerUserRequest.getLastName());
        user.setEmail(registerUserRequest.getEmail());
        user.setPassword(registerUserRequest.getPassword());
        user.setPhoneNumber(registerUserRequest.getPhoneNumber());
        return user;
    }

    private LoginRequestDTO getLoginRequestDTO() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("mkyong@gmail.com.aa");
        loginRequest.setPassword("pass5678910");
        return loginRequest;

    }

    private HttpServletResponse getHttpServletResponse() {
        HttpServletResponse response = new HttpServletResponse() {
            @Override
            public void addCookie(Cookie cookie) {
            }

            @Override
            public boolean containsHeader(String name) {
                return false;
            }

            @Override
            public String encodeURL(String url) {
                return null;
            }

            @Override
            public String encodeRedirectURL(String url) {
                return null;
            }

            @Override
            public String encodeUrl(String url) {
                return null;
            }

            @Override
            public String encodeRedirectUrl(String url) {
                return null;
            }

            @Override
            public void sendError(int sc, String msg) throws IOException {
            }

            @Override
            public void sendError(int sc) throws IOException {
            }

            @Override
            public void sendRedirect(String location) throws IOException {
            }

            @Override
            public void setDateHeader(String name, long date) {

            }

            @Override
            public void addDateHeader(String name, long date) {

            }

            @Override
            public void setHeader(String name, String value) {

            }

            @Override
            public void addHeader(String name, String value) {

            }

            @Override
            public void setIntHeader(String name, int value) {

            }

            @Override
            public void addIntHeader(String name, int value) {

            }

            @Override
            public void setStatus(int sc) {

            }

            @Override
            public void setStatus(int sc, String sm) {

            }

            @Override
            public int getStatus() {
                return 0;
            }

            @Override
            public String getHeader(String name) {
                return null;
            }

            @Override
            public Collection<String> getHeaders(String name) {
                return null;
            }

            @Override
            public Collection<String> getHeaderNames() {
                return null;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                return null;
            }

            @Override
            public void setCharacterEncoding(String charset) {

            }

            @Override
            public void setContentLength(int len) {

            }

            @Override
            public void setContentLengthLong(long length) {

            }

            @Override
            public void setContentType(String type) {

            }

            @Override
            public void setBufferSize(int size) {

            }

            @Override
            public int getBufferSize() {
                return 0;
            }

            @Override
            public void flushBuffer() throws IOException {

            }

            @Override
            public void resetBuffer() {

            }

            @Override
            public boolean isCommitted() {
                return false;
            }

            @Override
            public void reset() {

            }

            @Override
            public void setLocale(Locale loc) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

        };
        return response;
    }

    private Authentication getAuthentication(GeoRentUserDetails principal) {


        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return principal;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };

        return authentication;
    }
}