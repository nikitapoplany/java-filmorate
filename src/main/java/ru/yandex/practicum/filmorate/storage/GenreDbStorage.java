package ru.yandex.practicum.filmorate.storage;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper mapper;

    @Override
    public Set<Genre> findAll() {
        String query = """
                SELECT * FROM genre;
                """;
        return new HashSet<>(jdbcTemplate.query(query, mapper));
    }

    @Override
    public Genre findById(Integer genreId) {
        String query = """
                SELECT * FROM genre
                WHERE id = ?;
                """;
        return jdbcTemplate.queryForObject(query, mapper, genreId);
    }

    @Override
    public List<Genre> findGenreByFilmId(Integer filmId) {
        String query = """
                    SELECT g.*
                    FROM genre g
                    JOIN film_genre fg ON g.id = fg.genre_id
                    WHERE fg.film_id = ?
                    ORDER BY fg.genre_id;
                """;
        return jdbcTemplate.query(query, mapper, filmId);
    }


    @Override
    public void linkGenresToFilm(Integer filmId, Set<Integer> genreIdSet, boolean clearExisting) {
        StringBuilder insertQuery = new StringBuilder();

        for (Integer genreId : genreIdSet) {
            insertQuery.append(String.format("INSERT INTO film_genre (film_id, genre_id) VALUES (%d, %d);", filmId, genreId));
            insertQuery.append("\n");
        }

        if (clearExisting) {
            String deleteGenresOfFilmQuery = "DELETE FROM film_genre WHERE film_id = ?;";
            jdbcTemplate.update(deleteGenresOfFilmQuery, filmId);
        }
        jdbcTemplate.update(insertQuery.toString());
    }
}