package ru.yandex.practicum.filmorate.util;

import java.time.LocalDate;

import org.apache.commons.validator.routines.EmailValidator;

public class Validators {

    public static final int MAX_FILM_DESCRIPTION_LENGTH = 200;

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isValidString(String str) {
        return str != null && !str.isBlank();
    }

    public static boolean isValidFilmReleaseDate(LocalDate releaseDate) {
        if (releaseDate == null) {
            return true;
        }
        LocalDate minDate = LocalDate.of(1895, 12, 28);
        return releaseDate.isAfter(minDate);
    }

}
