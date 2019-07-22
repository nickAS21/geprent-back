package com.georent.domain;


import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {

    ADMIN(Code.ADMIN),
    USER(Code.USER);

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return description;
    }

    public class Code {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String USER = "ROLE_USER";
    }
}