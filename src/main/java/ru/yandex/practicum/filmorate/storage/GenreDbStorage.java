package ru.yandex.practicum.filmorate.storage;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper mapper;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

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
    public ArrayList<Genre> findGenreByFilmId(Integer filmId) {
        String query = """
                SELECT * FROM genre
                  WHERE id IN (
                  	SELECT genre_id
                  	FROM film_genre
                  	WHERE film_id = ?
                  );
                """;
        return new ArrayList<>(new LinkedHashSet<>(jdbcTemplate.query(query, mapper, filmId)));
    }

    @Override
    public void linkGenresToFilm(Integer filmId, Set<Integer> genreIdSet, boolean clearExisting) {
        String deleteGenresOfFilmQuery = "DELETE FROM film_genre WHERE film_id = ?;";
        StringBuilder insertQuery = new StringBuilder();

        for (Integer genreId : genreIdSet) {
            insertQuery.append(String.format("INSERT INTO film_genre (film_id, genre_id) VALUES (%d, %d);", filmId, genreId));
            insertQuery.append("\n");
        }
        if (clearExisting) {
            jdbcTemplate.update(deleteGenresOfFilmQuery, filmId);
        }
        jdbcTemplate.update(insertQuery.toString());
    }
}