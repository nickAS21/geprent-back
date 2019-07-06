package com.georent.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeoRentMailCredentials {

    private final MailConfigurationProperties properties;

    @Autowired
    public GeoRentMailCredentials(MailConfigurationProperties properties) {
        this.properties = properties;
    }

    public String getHost(){
        return properties.getHost();
    }

    public String getPort(){
        return properties.getPort();
    }

    public String getUserName(){
        return properties.getUserName();
    }

    public String getPass(){
        return properties.getPass();
    }

    public String getUrl(){
        return properties.getUrl();
    }

    //   port = 465
    public String getSsl() {
        return properties.getSsl();
    }
    //   port = 587
    public String getStarttls() {
        return properties.getStarttls();
    }

    public String getAuth() {
        return properties.getAuth();
    }

    public String getTimeout() {
        return properties.getTimeout();
    }

    public String getWritetimeout() {
        return properties.getWritetimeout();
    }

    public String getDebug() {
        return properties.getDebug();
    }

}
