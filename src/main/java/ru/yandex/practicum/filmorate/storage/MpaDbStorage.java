package ru.yandex.practicum.filmorate.storage;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mapper;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaRowMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }


    @Override
    public Set<Mpa> findAll() {
        String query = """
                SELECT * FROM mpa;
                """;
        return new HashSet<>(jdbcTemplate.query(query, mapper));
    }

    @Override
    public Mpa findById(Integer mpaId) {
        String query = """
                SELECT * FROM mpa
                WHERE id = ?;
                """;
        return jdbcTemplate.queryForObject(query, mapper, mpaId);
    }
}
