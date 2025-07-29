package ru.yandex.practicum.filmorate.util;

import org.apache.commons.validator.routines.EmailValidator;

public class Validators {

    public static boolean validateEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean validateString(String str) {
        return str != null && !str.isBlank();
    }

}
