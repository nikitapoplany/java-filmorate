package ru.yandex.practicum.filmorate.storage;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorage implements FilmStorage {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper mapper;
    private final FilmMapper filmMapper;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final LikeService likeService;

    @Override
    public List<Film> findAll() {
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
        Film film = result.getFirst();
        Mpa mpa = mpaService.findById(film.getMpa().getId());
        ArrayList<Genre> genre = genreService.findGenreByFilmId(filmId);
        List<Integer> likes = likeService.getLikesByFilmId(filmId);
        film.setMpa(mpa);
        film.setGenres(genre);
        film.getLikes().addAll(likes);
        return film;
    }

    @Override
    public Film create(FilmCreateDto filmCreateDto) {
        Film film = filmMapper.toEntity(filmCreateDto);
        String query = """
                INSERT INTO film (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
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
                    ps.setInt(5, film.getMpa().getId());
                    return ps;
                }, keyHolder);

        if (Optional.ofNullable(keyHolder.getKey()).isPresent()) {
            film.setId(keyHolder.getKey().intValue());
            log.info("Добавлен новый фильм: {}", filmCreateDto);
            Set<Integer> genreIdList = film.getGenres()
                    .stream()
                    .mapToInt(Genre::getId)
                    .boxed()
                    .collect(Collectors.toSet());
            genreService.linkGenreToFilm(film.getId(), genreIdList);
            return film;
        }

        throw new RuntimeException("Непредвиденная ошибка при добавлении фильма.");
    }

    @Override
    public Film update(Film filmUpdate, Film filmOriginal) {
        for (Field field : filmOriginal.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(filmUpdate);
                if (value == null) {
                    field.set(filmUpdate, field.get(filmOriginal));
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }

        String query = """
                    UPDATE film
                    SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
                    WHERE film.id = ?;
                """;

        int updatedRows = jdbcTemplate.update(
                query,
                filmUpdate.getName(),
                filmUpdate.getDescription(),
                filmUpdate.getReleaseDate(),
                filmUpdate.getDuration(),
                filmUpdate.getMpa().getId(),
                filmUpdate.getId()
        );
        if (updatedRows != 0) {
            log.info("Обновлён фильм id {}. Новое значение: {}", filmOriginal.getId(), filmOriginal);
        } else {
            LoggedException.throwNew(
                    new NotFoundException(
                            String.format("Не удалось обновить фильм id %d. "
                                          + "Фильм не найден.", filmUpdate.getId())), getClass());
        }

        return filmUpdate;
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
                    SELECT f.*
                    FROM film AS f
                    LEFT JOIN "like" AS l ON f.id = l.film_id
                    GROUP BY f.id
                    ORDER BY COUNT(l.id) DESC
                    LIMIT ?;
                """;
        return jdbcTemplate.query(query, mapper, size).stream().peek(film -> {
            List<Integer> likes = likeService.getLikesByFilmId(film.getId());
            film.getLikes().addAll(likes);
        }).toList();
    }
}
