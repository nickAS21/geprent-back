package com.georent.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Message {

    ERROR("Error..."),
    USER_NOT_FOUND_ERROR("User not found!"),

    MAIL_SENT("Mail sent to successfully!"),
    MAIL_START_BROWSER("Browser start to successfully!"),
    MAIL_NOT_SENT("Mail not sent!"),
    MAIL_SENT_SUB_TXT_FORGOT("Восстановление доступа к cвоей  учетой записи на сайте \"Прокат рядом\""),
    MAIL_SENT_TXT_FORGOT("Если вы забыли пароль, мы отправим вам ссылку для создания нового пароля, чтобы вы могли восстановить доступ к cвоей  учетой записи на сайте \"Прокат рядом\"<br>"),
    MAIL_SENT_TXT_FORGOT_LINK(" Для изменения пароля перейти по ссылке <br> "),
    MAIL_SENT_TXT_FORGOT_NOTHING(" Если Вы не запрашивали обновление пароля, ничего не делайте <br>"),

    SECURITY_CONTEXT_ERROR("Could not set user authentication in security context!"),
    ACTIVATION_USER_ERROR("The account has already been activated!"),
    LOGIN_USER_ERROR("User is not activated!"),
    UNAUTHORIZED_ERROR("You are not authorized to access this resource!"),
    INVALID_TOKEN_ERROR("Invalid JWT token!"),
    INVALID_PASSWORD("Invalid password!"),

    SUCCESS_REGISTRATION("User registered successfully!"),
    SUCCESS_UPDATE_USER("User data updated successfully!"),
    SUCCESS_DELETE_USER("User deleteed successfully!"),
    INVALID_GET_USER_EMAIL("No user with email:  "),
    INVALID_GET_USER_ID("No user with ID: "),

    SUCCESS_UPDATE_PASSWORD("Password updated successfully!"),
    SUCCESS_SAVE_LOT("Lot saved successfully!"),
    INVALID_SAVE_LOT("Not saved lot !"),
    SUCCESS_DELETE_LOT("Lot deleted successfully!"),
    SUCCESS_DELETE_LOTS("All lots of user deleted successfully!"),
    INVALID_GET_LOT_ID("Not found lot with ID: "),
    INVALID_GET_LOT_ID_USER(" for user with ID: "),
    INVALID_SAVE_FILE("Unable to save the file."),

//    INVALID_FILE_EXTENSION_JPG("Only JPG images are accepted."),
//    INVALID_FILE_SIZE("Size is too big."),
    INVALID_FILE_NULL("File must not be empty!"),

    INVALID_FILE_SAVE_TMP("Unable to save file tmp."),

    INVALID_PICTURE_LOAD_AMAZONE_SERVICES("Amazon S3 couldn't process."),
    INVALID_PICTURE_LOAD_SDK_CLIENT("Client couldn't parse the response from Amazon S3.");

    private String description;

    public String getDescription () {
        return this.description;
    }


}
