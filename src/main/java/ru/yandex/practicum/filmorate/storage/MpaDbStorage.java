package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mapper;

    @Override
    public List<Mpa> findAll() {
        String query = """
                SELECT * FROM mpa;
                """;
        return jdbcTemplate.query(query, mapper);
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
