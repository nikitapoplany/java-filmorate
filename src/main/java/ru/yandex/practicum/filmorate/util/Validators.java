package ru.yandex.practicum.filmorate.util;

import java.time.LocalDate;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class Validators {
    public static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
    @Autowired
    private static JdbcTemplate jdbcTemplate;

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

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

    public static boolean isExistingLike(Integer filmId, Integer userId) {
        String query = """
                CASE
                	WHEN (SELECT count(*) FROM "like" WHERE user_id = ? AND film_id = ?) > 0 THEN TRUE
                	ELSE FALSE
                END;
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, Boolean.class, userId, filmId));
    }

    public static boolean isValidMpa(Integer mpaId) {
        String query = """
                    SELECT
                    CASE
                    	WHEN ? IN (SELECT id FROM mpa) THEN TRUE
                    	ELSE FALSE
                    END;
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, Boolean.class, mpaId));
    }

    public static boolean isValidGenre(Integer genreId) {
        String query = """
                    SELECT
                    CASE
                    	WHEN ? IN (SELECT id FROM genre) THEN TRUE
                    	ELSE FALSE
                    END;
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, Boolean.class, genreId));
    }
}
