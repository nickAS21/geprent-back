package com.georent.service;

import com.georent.config.JwtConfigurationProperties;
import com.georent.config.MailConfigurationProperties;
import com.georent.domain.GeoRentUser;
import com.georent.domain.GeoRentUserDetails;
import com.georent.domain.UserRole;
import com.georent.dto.*;
import com.georent.exception.BasicExceptionHandler;
import com.georent.exception.ForgotException;
import com.georent.exception.RegistrationSuchUserExistsException;
import com.georent.message.GeoRentIHttpStatus;
import com.georent.message.Message;
import com.georent.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;

@Service
public class AuthenticationService {

    public static final String BEARER = "Bearer ";

    private final AuthenticationManager authManager;
    private final JwtProvider jwtProvider;
    private final JwtConfigurationProperties jwtProperties;
    private final GeoRentUserService userService;
    private final JavaMailSender javaMailSender;
    private final MailConfigurationProperties mailProps;

    @Autowired
    public AuthenticationService(final AuthenticationManager authManager,
                                 final JwtProvider jwtProvider,
                                 final JwtConfigurationProperties jwtProperties,
                                 final GeoRentUserService userService,
                                 final JavaMailSender javaMailSender,
                                 final MailConfigurationProperties mailProps) {
        this.authManager = authManager;
        this.jwtProvider = jwtProvider;
        this.jwtProperties = jwtProperties;
        this.userService = userService;
        this.javaMailSender = javaMailSender;
        this.mailProps = mailProps;
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

    public GenericResponseDTO registerUser(final RegistrationRequestDTO registerUserRequest) {
        GeoRentUser geoRentUser = registerNewUserAccount(registerUserRequest);
        GenericResponseDTO<GeoRentUserDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_REGISTRATION.getDescription());
        responseDTO.setBody(mapToGeoRentUserDTO(geoRentUser));
        return responseDTO;
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

    public ResponseEntity<?> forgotPasswordUser(String email,
                                                String serverApi,
                                                HttpServletRequest request) {
        GeoRentUser geoRentUser = userService.getUserByEmail(email)
                .orElseThrow(() -> new ForgotException(Message.USER_NOT_FOUND_ERROR.getDescription()));
        String accessToken = jwtProvider.generateAccessToken(GeoRentUserDetails.create(geoRentUser));
        String tokenType = "Bearer";
        String url = serverApi + "/forgot" +
                "?tokenType=" + tokenType +
                "&accessToken=" + accessToken;
        sendmail(email, Message.MAIL_SENT_SUB_TXT_FORGOT.getDescription(), getSetTextForMailForgot (url));
        BasicExceptionHandler.GenericResponse<String> response = new BasicExceptionHandler.GenericResponse<>();
        response.setMethod(request.getMethod());
        response.setCause(Message.MAIL_SENT_FORGOT.getDescription());
        response.setPath(request.getRequestURI());
        response.setBody(Message.MAIL_SENT.getDescription());
        response.setStatusCode(HttpStatus.MOVED_PERMANENTLY.value());
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).body(response);
    }


    @Transactional
    public ResponseEntity<?> forgotPasswordSave(Principal principal, String newPassword, HttpServletRequest request) {
        GeoRentUser geoRentUser = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
        geoRentUser.setPassword(newPassword);
        userService.saveNewUser(geoRentUser);

        // send email
        sendmail(geoRentUser.getEmail(), Message.SUCCESS_UPDATE_PASSWORD.getDescription(), Message.MAIL_SENT_FORGOT_AFTER_NOT.getDescription());
        BasicExceptionHandler.GenericResponse<String> response = new BasicExceptionHandler.GenericResponse<>();
        response.setMethod(request.getMethod());
        response.setCause(Message.MAIL_SENT_FORGOT_AFTER.getDescription());
        response.setPath(request.getRequestURI());
        response.setBody(Message.MAIL_SENT.getDescription());
        response.setStatusCode(HttpStatus.MOVED_PERMANENTLY.value());
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).body(response);
    }

    public GeoRentUserDTO mapToGeoRentUserDTO(GeoRentUser user) {
        GeoRentUserDTO dto = new GeoRentUserDTO();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
    }

    public void sendmail(String to_mail, String setSubject, String setText) {
        String username = mailProps.getUsername();
        MimeMessage msg = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, false);
            helper.setFrom(username);
            helper.setTo(to_mail);
            helper.setSubject(setSubject);
            helper.setText(setText, true);
            helper.setSentDate(new Date());
        } catch (MessagingException e) {
            throw new ForgotException(Message.MAIL_NOT_SENT.getDescription() + " " + e.getMessage());
        }
        javaMailSender.send(msg);
    }

    private String getSetTextForMailForgot (String url) {
        String content = "<a href='" + url + "'>" +  Message.MAIL_SENT_TXT_FORGOT_LINK.getDescription() + "</a><br>";
        String setText = "<p> " + Message.MAIL_SENT_TXT_FORGOT.getDescription() + content + "  </p>";
        setText += "<p>" + Message.MAIL_SENT_TXT_FORGOT_NOTHING.getDescription() + "</p>";
        return  setText;

    }

    private Multipart getMultipartForgot(String url) throws MessagingException, IOException {

        url += "/signup";
        String content = "<a href='" + url + "'>" + url + "</a>";

        MimeBodyPart messageUrlPart = new MimeBodyPart();
        messageUrlPart.setContent(Message.MAIL_SENT_TXT_FORGOT_NOTHING.getDescription()
                + Message.MAIL_SENT_TXT_FORGOT_LINK.getDescription()
                + content, "text/html; charset=utf-8");

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        String picture = "шуруповерт";
        messageBodyPart.setContent(" <br /> The  picture: " + picture + "", "text/html; charset=utf-8");

        MimeBodyPart attachPart = new MimeBodyPart();
        attachPart.attachFile("/mnt/data/Hillel/Kino_Procat/Png_Jpg/shurupovert_600_717.png");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageUrlPart);
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachPart);
        return multipart;
    }


}
