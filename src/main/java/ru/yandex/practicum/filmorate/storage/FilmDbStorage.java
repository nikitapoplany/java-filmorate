package ru.yandex.practicum.filmorate.storage;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

@Component
public class FilmDbStorage implements FilmStorage {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper mapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public Collection<Film> findAll() {
        String query = "SELECT * FROM film;";
        return jdbcTemplate.query(query, mapper);
    }

    @Override
    public Film findById(Integer filmId) {
        String query = "SELECT * FROM film f WHERE f.id = ?;";
        List<Film> result = jdbcTemplate.query(query, mapper, filmId);
        if (result.isEmpty()) {
            LoggedException.throwNew(
                    new NotFoundException(
                            String.format("Не удалось получить фильм id %d. "
                                          + "Фильм не найден.", filmId)), getClass());
        }
        return result.getFirst();
    }

    @Override
    public Film create(FilmCreateDto filmCreateDto) {
        Film film = FilmMapper.toEntity(filmCreateDto);
        String query = """
                INSERT INTO film (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_RATING_ID)
                VALUES(?, ?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                    ps.setInt(4, film.getDuration());
                    ps.setInt(5, film.getMpaId());
                    return ps;
                }, keyHolder);

        if (Optional.ofNullable(keyHolder.getKey()).isPresent()) {
            film.setId(keyHolder.getKey().intValue());
            log.info("Добавлен новый фильм: {}", filmCreateDto);
            return film;
        }

        throw new RuntimeException("Непредвиденная ошибка при добавлении фильма.");
    }

    @Override
    public Film update(Film filmUpdate, Film filmOriginal) {
        String copy = filmOriginal.toString();

        for (Field field : filmUpdate.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(filmUpdate);
                if (value != null) {
                    field.set(filmOriginal, value);
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }

        String query = """
                UPDATE film
                SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?
                WHERE film.id = ?;
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int updatedRows = jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
                    ps.setString(1, filmOriginal.getName());
                    ps.setString(2, filmOriginal.getDescription());
                    ps.setDate(3, Date.valueOf(filmOriginal.getReleaseDate()));
                    ps.setInt(4, filmOriginal.getDuration());
                    ps.setInt(5, filmOriginal.getMpaId());
                    ps.setInt(6, filmOriginal.getId());
                    return ps;
                }, keyHolder);
        if (updatedRows != 0) {
            log.info("Обновлён фильм {}. Новое значение: {}", copy, filmOriginal);
        } else {
            LoggedException.throwNew(
                    new NotFoundException(
                            String.format("Не удалось обновить фильм id %d. "
                                          + "Фильм не найден.", filmUpdate.getId())), getClass());
        }

        return filmOriginal;
    }

    @Override
    public Integer delete(Integer filmId) {
        String query = "DELETE FROM film WHERE id = ?";
        int deletedRows = jdbcTemplate.update(query);
        if (deletedRows != 0) {
            log.info("Удалён фильм id {}", filmId);
        } else {
            LoggedException.throwNew(
                    new NotFoundException(
                            String.format("Не удалось удалить фильм id %d. "
                                          + "Фильм не найден.", filmId)), getClass());
        }
        return filmId;
    }

    @Override
    public List<Film> findTopLiked(int size) {
        String query = """
                SELECT * FROM film
                WHERE id IN (
                	SELECT f.id, count(*) AS LIKE_COUNT FROM film f
                	GROUP BY f.id
                	ORDER BY LIKE_COUNT
                	LIMIT ?;
                );
                """;
        return jdbcTemplate.query(query, mapper, size);
    }
}
