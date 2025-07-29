package ru.yandex.practicum.filmorate.film;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.abstraction.AbstractService;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.LoggedException;

import static ru.yandex.practicum.filmorate.util.Validators.validateString;

@Service
public class FilmService extends AbstractService<Film> {

    public List<Film> findAll() {
        return (List<Film>) mapEntityStorage.values();
    }

    public Film create(Film film) {
        if (!isValidReleaseDate(film)) {
            LoggedException.throwNew(
                    new ValidationException(String.format("Дата создания фильма не может быть ранее 28 декабря 1895 г."
                                    + " Некорректная дата - %s",
                            film.getReleaseDate())), getClass());
        }
        film.setId(getNextId());
        mapEntityStorage.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    public Film update(Film film) {
        Film oldFilm = Optional.ofNullable(film.getId()).map(mapEntityStorage::get).orElse(null);

        if (oldFilm == null) {
            LoggedException.throwNew(
                    new NotFoundException(String.format("Ошибка при обновлении фильма id=%d: фильм не найден",
                            film.getId())), getClass());
        }

        if (film.getReleaseDate() != null) {
            if (!isValidReleaseDate(film)) {
                LoggedException.throwNew(
                        new ValidationException(String.format("Ошибка при обновлении фильма id=%d: фильм не найден",
                                film.getId())), getClass());
            }
            oldFilm.setReleaseDate(film.getReleaseDate());
        }

        if (validateString(film.getName())) {
            oldFilm.setName(film.getName());
        }

        if (film.getDuration() != null) {
            oldFilm.setDuration(film.getDuration());
        }

        if (validateString(film.getDescription()) && film.getDescription().length() <= 200) {
            oldFilm.setDescription(film.getDescription());
        }

        return oldFilm;
    }

    private boolean isValidReleaseDate(Film film) {
        LocalDate minDate = LocalDate.of(1895, 12, 28);
        return film.getReleaseDate().isAfter(minDate);
    }
}
