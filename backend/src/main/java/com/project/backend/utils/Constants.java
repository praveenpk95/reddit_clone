package com.project.backend.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String ACTIVATION_EMAIL    = "http://localhost:8080/api/auth/accountVerification";
    public static final String ROLE_USER           = "USER";
    public static final String ERROR_NO_USER_FOUND = "No user found with Username : ";
}