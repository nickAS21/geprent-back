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

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;

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

    public ResponseEntity<?> forgotUser(final LoginForgotPasswordDTO loginForgot,
                                        HttpServletRequest request) {
//        GeoRentUser geoRentUser = userService.getUserByEmail(loginForgot.getEmail())
//                .orElseThrow(() -> new ForgotException(Message.USER_NOT_FOUND_ERROR.getDescription()));
        try {
            sendmail( loginForgot.getEmail(), getMultipartForgot (loginForgot.getUrl()));
        } catch (MessagingException | IOException e) {
            throw new  ForgotException(Message.MAIL_NOT_SENT.getDescription() + " " + e.getMessage());
        }
        BasicExceptionHandler.GenericResponse<String> response = new BasicExceptionHandler.GenericResponse<>();
        response.setMethod(request.getMethod());
        response.setCause(Message.MAIL_SENT.getDescription());
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

    public void sendmail(String to_mail, Multipart multipart) throws MessagingException {

        String username = mailProps.getUsername();

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(username);
        helper.setTo(to_mail);
        helper.setSubject(Message.MAIL_SENT_SUB_TXT.getDescription());
        helper.setText("test test");

//        msg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to_mail));
//        msg.setSubject(Message.MAIL_SENT_SUB_TXT.getDescription());
//        msg.setSentDate(new Date());
//        msg.setContent(multipart);

        javaMailSender.send(msg);

//        Transport transport = msgSession.getTransport();
//        transport.connect(msgSession.getProperty("mail.smtp.host"), Integer.valueOf(msgSession.getProperty("mail.smtp.port")),
//                userName, msgSession.getProperty("mail.pass"));
//        transport.sendMessage(msg, msg.getRecipients(javax.mail.Message.RecipientType.TO));
//        transport.close();
    }

    private Multipart getMultipartForgot (String url) throws MessagingException, IOException {

        url += "/signup";
        String content = "<a href='" + url + "'>" + url + "</a>";

        MimeBodyPart messageUrlPart = new MimeBodyPart();
        messageUrlPart.setContent(Message.MAIL_SENT_BODY_TXT.getDescription()
                + Message.MAIL_SENT_BODY_TXT_LINK.getDescription()
                + content, "text/html; charset=utf-8");

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        String picture = "шуруповерт";
        messageBodyPart.setContent( " <br /> The  picture: " + picture + "", "text/html; charset=utf-8");

        MimeBodyPart attachPart = new MimeBodyPart();
        attachPart.attachFile("/mnt/data/Hillel/Kino_Procat/Png_Jpg/shurupovert_600_717.png");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageUrlPart);
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachPart);
        return multipart;
    }

//    public void sendmailOld(String to_mail, String url) throws MessagingException, IOException {
//        // Create a Simple MailMessage.
////        SimpleMailMessage message = new SimpleMailMessage();
////
////        message.setTo(to_mail);
////        message.setSubject("Test Simple Email");
////        message.setText("Hello, Im testing Simple Email");
//
//        // Send Message!
////        this.emailSender.send(message);
//
//        /**
//         spring.mail.host=smtp.gmail.com
//         spring.mail.port=587
//         spring.mail.username=username
//         spring.mail.password=password
//
//         # Other properties
//         установка mail.smtp.ehlo в false отключает попытки использовать EHLO
//         установка mail.smtp.auth в true включает проверку наличия пароля userName
//         spring.mail.properties.mail.smtp.auth=true
//         spring.mail.properties.mail.smtp.connectiontimeout=5000
//         spring.mail.properties.mail.smtp.timeout=5000
//         spring.mail.properties.mail.smtp.writetimeout=5000
//
//         # TLS , port 587
//         spring.mail.properties.mail.smtp.starttls.enable=true
//
//         # SSL, post 465
//         #spring.mail.properties.mail.smtp.socketFactory.port = 465
//         #spring.mail.properties.mail.smtp.socketFactory.class = javax.net.ssl.SSLSocketFactory
//         **/
//
//
////        String subText = "Sub: Tutorials point email";
////        String contenBodyTxt = "Content Body: Tutorials point email";
//
//
////        Properties props = new Properties();
//
////        String host= "imap.solk.pl";
////        String host = "smtp.gmail.com";
////        String port = "465";
////        String port = "587";
////        String from_gmail_com = "nick@avalr.com.ua";
////        String from_gmail_com = "prokatradom@gmail.com";
////        String from_gmail_com = "nick.kulikov21@gmail.com";
////        String pass = "tnv2606";
////        String pass = "procatAS2107";
////        String pass = "procatAS2107";
////        String pass = "pasnickAS2107";
////        String to_mail = "nick.kulikov21@gmail.com";
////        String to_mail = "prokatradomuser@mail.ru";
////        String to_gmail_com = "nick@avalr.com.ua";
////        props.put("mail.smtp.ssl.enable", "true");    // port = 465
////        props.put("mail.smtp.starttls.enable", "true"); // port = 587
////
////        props.put("mail.smtp.auth", "false");
////        props.put("mail.smtp.timeout", "5000");
////        props.put("mail.smtp.writetimeout", "5000");
////        props.put("mail.debug", "true");
////        props.put("mail.smtp.host", host);
////        props.put("mail.smtp.port", port);
////        props.put("mail.username", from_gmail_com);
////        props.put("mail.pass", pass);
//
//
////        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
////            protected PasswordAuthentication getPasswordAuthentication() {
////                return new PasswordAuthentication(from_gmail_com, pass);
////            }
////        });
//
////        MimeMessage msg = new MimeMessage(session);
//
//        String subText = "Если вы забыли пароль, мы отправим вам ссылку для создания нового пароля, чтобы вы могли восстановить доступ к cвоей  учетой записи на сайте \"Прокат рядом\"";
//        String contenBodyTxt = "Если Вы не запрашивали обновление пароля, ничего не делайте";
//        MimeMessage msg = this.message;
//        Session msgSession = msg.getSession();
//        String userName = msgSession.getProperty("mail.username");
//        msg.setFrom(new InternetAddress(userName, false));
//
//        msg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to_mail));
//        msg.setSubject(subText);
//        msg.setSentDate(new Date());
//        // URL
//         url += "/signup";
//        String content = "<a href='" + url + "'>" + url + "</a>";
//
//        MimeBodyPart messageUrlPart = new MimeBodyPart();
//        messageUrlPart.setContent(contenBodyTxt + " <br />Для изменения пароля перейти по ссылке <br />" + " " + content, "text/html; charset=utf-8");
//
//        MimeBodyPart messageBodyPart = new MimeBodyPart();
//        String picture = "шуруповерт";
//        messageBodyPart.setContent( " <br /> The  picture: " + picture + "", "text/html; charset=utf-8");
//
//
//        MimeBodyPart attachPart = new MimeBodyPart();
//        attachPart.attachFile("/mnt/data/Hillel/Kino_Procat/Png_Jpg/shurupovert_600_717.png");
//
//        Multipart multipart = new MimeMultipart();
//        multipart.addBodyPart(messageUrlPart);
//        multipart.addBodyPart(messageBodyPart);
//        multipart.addBodyPart(attachPart);
//        msg.setContent(multipart);
//
//        Transport transport = msgSession.getTransport();
//        transport.connect(msgSession.getProperty("mail.smtp.host"), Integer.valueOf(msgSession.getProperty("mail.smtp.port")),
//                userName, msgSession.getProperty("mail.pass"));
//        transport.sendMessage(msg, msg.getRecipients(javax.mail.Message.RecipientType.TO));
//        transport.close();
//
//
////        Session mSession = Session.getDefaultInstance(new Properties());
////        Transport mTransport = null;
////        mTransport = mSession.getTransport("smtp");
////        mTransport.connect("imap.solk.pl", 465, from_gmail_com, passAval);
////        MimeMessage mMessage = new MimeMessage(mSession);
////        mTransport.sendMessage(mMessage,  mMessage.getAllRecipients());
////        mTransport.close();
//    }


 }
