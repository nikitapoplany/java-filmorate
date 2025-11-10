package ru.yandex.practicum.filmorate.util;

import java.time.LocalDate;

public class Validators {

    public static final int MAX_FILM_DESCRIPTION_LENGTH = 200;

    public static boolean isValidString(String str) {
        return str != null && !str.isBlank();
    }

    public static boolean isValidLogin(String login) {
        return isValidString(login) && !login.contains(" ");
    }

    public static boolean isValidFilmReleaseDate(LocalDate releaseDate) {
        if (releaseDate == null) {
            return true;
        }
        LocalDate minDate = LocalDate.of(1895, 12, 28);
        return releaseDate.isAfter(minDate);
    }
}
