package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeStorage;

@Component
public class LikeDbStorage implements LikeStorage {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void setLike(Integer filmId, Integer userId) {
        String query = """
                INSERT INTO "like" (film_id, user_id)
                VALUES (?, ?);
                """;
        jdbcTemplate.update(query);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        String query = """
                DELETE FROM "like"
                WHERE film_id = ?
                AND user_id = ?;
                """;
        jdbcTemplate.update(query, filmId, userId);
    }
}
