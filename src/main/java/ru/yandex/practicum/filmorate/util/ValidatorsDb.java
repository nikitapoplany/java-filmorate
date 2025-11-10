package ru.yandex.practicum.filmorate.util;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidatorsDb {
    private final JdbcTemplate jdbcTemplate;

    public boolean isExistingLike(Integer filmId, Integer userId) {
        String query = """
                    SELECT
                    CASE
                        WHEN (SELECT count(*) FROM "like" WHERE user_id = ? AND film_id = ?) > 0 THEN TRUE
                        ELSE FALSE
                    END;
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, Boolean.class, userId, filmId));
    }

    public boolean isValidMpa(Integer mpaId) {
        String query = """
                    SELECT
                    CASE
                    	WHEN ? IN (SELECT id FROM mpa) THEN TRUE
                    	ELSE FALSE
                    END;
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, Boolean.class, mpaId));
    }

    public boolean isValidGenre(Integer genreId) {
        String query = """
                    SELECT
                    CASE
                    	WHEN ? IN (SELECT id FROM genre) THEN TRUE
                    	ELSE FALSE
                    END;
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, Boolean.class, genreId));
    }

    public boolean isValidFriend(Integer userIdA, Integer userIdB) {
        String query = """
                SELECT CASE
                	WHEN (SELECT COUNT(*) FROM friends WHERE request_from_id = ? AND request_to_id = ?) > 0 THEN TRUE
                	ELSE FALSE
                END;
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, Boolean.class, userIdA, userIdB));
    }
}
