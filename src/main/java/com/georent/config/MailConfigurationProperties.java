package com.georent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "mail")
public class MailConfigurationProperties {
    private String host;
    private String port;
    private String userName;
    private String pass;
    private String url;
    //   port = 465
    private String ssl;
    //   port = 587
    private String starttls;
    private String auth;
    private String timeout;
    private String writetimeout;
    private String debug;
}

