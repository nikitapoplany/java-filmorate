package ru.yandex.practicum.filmorate.storage;

import java.util.HashSet;
import java.util.Set;

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
}