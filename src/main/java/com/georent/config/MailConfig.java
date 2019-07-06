package com.georent.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Configuration
public class MailConfig {

    private final GeoRentMailCredentials geoRentMailCredentials;

    @Autowired
    public MailConfig(GeoRentMailCredentials geoRentMailCredentials) {
        this.geoRentMailCredentials = geoRentMailCredentials;
    }

    /**
     *         Properties:
     *          # Main properties
     *          spring.mail.username = username
     *          spring.mail.password = password
     *              # gmail properties
     *          spring.mail.host = smtp.gmail.com
     *          spring.mail.port = 587
     *
     *          # Other properties
     *          установка mail.smtp.ehlo в false отключает попытки использовать EHLO
     *          установка mail.smtp.auth в true включает проверку наличия пароля userName
     *          spring.mail.properties.mail.smtp.auth = true
     *          spring.mail.properties.mail.smtp.connectiontimeout = 5000
     *          spring.mail.properties.mail.smtp.timeout = 5000
     *          spring.mail.properties.mail.smtp.writetimeout = 5000
     *              # To watch logs in the terminal
     *          spring.mail.properties.mail.debug = true
     *
     *          # TLS , port 587
     *          spring."mail.smtp.port = 587
     *          spring.mail.properties.mail.smtp.starttls.enable = true
     *
     *          # SSL, post 465
     *          #spring.mail.properties.mail.smtp.ssl.enable = true
     *          #spring.mail.properties.mail.smtp.socketFactory.port = 465
     *          #spring.mail.properties.mail.smtp.socketFactory.class = javax.net.ssl.SSLSocketFactory
     * @return
     */
    @Bean
    public MimeMessage getMailSession () {
        String userName = this.geoRentMailCredentials.getUserName();
        String pass = this.geoRentMailCredentials.getPass();


        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", this.geoRentMailCredentials.getStarttls());
        props.put("mail.smtp.auth", this.geoRentMailCredentials.getAuth());
        props.put("mail.smtp.timeout", this.geoRentMailCredentials.getTimeout());
        props.put("mail.smtp.writetimeout", this.geoRentMailCredentials.getWritetimeout());
        props.put("mail.debug", this.geoRentMailCredentials.getDebug());
        props.put("mail.smtp.host", this.geoRentMailCredentials.getHost());
        props.put("mail.smtp.port", this.geoRentMailCredentials.getPort());
        props.put("mail.username", userName);
        props.put("mail.pass", pass);


        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, pass);
            }
        });
        MimeMessage msg = new MimeMessage(session);
        return  msg;
    }
}
