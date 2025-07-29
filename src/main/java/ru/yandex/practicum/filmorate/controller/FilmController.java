package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.*;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (!isValidReleaseDate(film)) {
            log.error("Дата создания фильма не может быть ранее 28 декабря 1895 г. Некорректная дата - {}",
                    film.getReleaseDate());
            throw new ValidationException(String.format("Дата создания фильма не может быть ранее 28 декабря 1895 г."
                            + " Некорректная дата - %s",
                    film.getReleaseDate()));
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        Film oldFilm = Optional.ofNullable(film.getId()).map(films::get).orElseThrow(() -> {
            log.error("Ошибка при обновлении фильма id={}: фильм не найден", film.getId());
            return new ValidationException(String.format("Ошибка при обновлении фильма id=%d: фильм не найден",
                    film.getId()));
        });

        if (film.getReleaseDate() != null) {
            if (!isValidReleaseDate(film)) {
                log.error("Ошибка при обновлении фильма id={}: некорректная дата выхода", film.getId());
                throw new ValidationException(String.format("Ошибка при обновлении фильма id=%d: фильм не найден",
                        film.getId()));
            }
            oldFilm.setReleaseDate(film.getReleaseDate());
        }

        if (isValidString(film.getName())) {
            oldFilm.setName(film.getName());
        }

        if (film.getDuration() != null) {
            oldFilm.setDuration(film.getDuration());
        }

        if (isValidString(film.getDescription()) && film.getDescription().length() <= 200) {
            oldFilm.setDescription(film.getDescription());
        }

        return oldFilm;
    }

    private boolean isValidReleaseDate(Film film) {
        LocalDate minDate = LocalDate.of(1895, 12, 28);
        return film.getReleaseDate().isAfter(minDate);
    }

    private boolean isValidString(String str) {
        return str != null && !str.isBlank();
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
