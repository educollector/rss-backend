package com.ola.model;

/**
 * Created by olaskierbiszewska on 31.01.16.
 */
public class StringsManager {

    public StringsManager() {
    }

    public static String stringNoUserInDb() {
        return "{\"message\":\"noUser\"}";
    }

    public static String stringInvalidPassword() { return "{\"message\":\"invalidPassword\"}";}

    public static String stringInvalidNameOrPassword() { return "{\"message\":\"invalidNameOrPassword\"}";}

    public static String stringNickNotAvailable() {
        return "{\"message\":\"userNameNotAvailable\"}";
    }

    public static String stringUserLoggedWithToken() {
        return "{\"message\":\"logged\", \"token\":\"";
    }

    public static String stringRegistrationSucceedWithToken() {
        return "{\"message\":\"registered\", \"token\":\"";
    }

    public static String stringRegistrationFailed() {
        return "{\"message\":\"registrationFailed\"";
    }

}
