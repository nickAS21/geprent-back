package com.georent.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Message {

    SEND_MAIL_ERROR("Error sending html message!"),
    USER_NOT_FOUND_ERROR("User not found!"),
    SECURITY_CONTEXT_ERROR("Could not set user authentication in security context!"),
    ACTIVATION_USER_ERROR("The account has already been activated!"),
    LOGIN_USER_ERROR("User is not activated!"),
    REGISTRATION_USER_ERROR("Email address already in use!"),
    UNAUTHORIZED_ERROR("You are not authorized to access this resource!"),
    INVALID_TOKEN_ERROR("Invalid JWT token!"),
    INVALID_PASSWORD("Invalid password!"),

    SUCCESS_REGISTRATION("User registered successfully!"),
    SUCCESS_UPDATE_USER("User data updated successfully!"),
    SUCCESS_UPDATE_PASSWORD("Password updated successfully!");

    private String description;
}
