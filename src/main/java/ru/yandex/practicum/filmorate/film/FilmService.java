package ru.yandex.practicum.filmorate.film;

import java.lang.reflect.Field;
import java.util.Collection;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.abstraction.AbstractService;
import ru.yandex.practicum.filmorate.dto.film.*;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import static ru.yandex.practicum.filmorate.util.Validators.MAX_FILM_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.util.Validators.isValidFilmReleaseDate;

@Service
public class FilmService extends AbstractService<Film> {

    public Collection<Film> findAll() {
        return mapEntityStorage.values();
    }

    public Film create(FilmCreateDto filmCreateDto) {
        Film film = FilmMapper.toEntity(filmCreateDto);
        film.setId(getNextId());
        mapEntityStorage.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    public Film update(FilmUpdateDto filmUpdateDto) {
        if (!mapEntityStorage.containsKey(filmUpdateDto.getId())) {
            LoggedException.throwNew(
                    new NotFoundException(String.format("Ошибка при обновлении фильма id=%d: фильм не найден",
                            filmUpdateDto.getId())), getClass());
        }

        Film filmUpdate = FilmMapper.toEntity(filmUpdateDto);
        Film film = mapEntityStorage.get(filmUpdate.getId());

        if (!isValidFilmReleaseDate(filmUpdate.getReleaseDate())) {
            LoggedException.throwNew(
                    new ValidationException(String.format("Дата создания фильма не может быть ранее 28 декабря 1895 г."
                                    + " Некорректная дата - %s",
                            filmUpdate.getReleaseDate())), getClass());
        }

        if (filmUpdate.getDescription() != null && filmUpdate.getDescription().length() > 200) {
            LoggedException.throwNew(
                    new ValidationException(String.format("Количество символов в описании фильма (%d симв.) не должно "
                                    + "превышать максимально допустимое (%d симв.)",
                            filmUpdate.getDescription().length(), MAX_FILM_DESCRIPTION_LENGTH)), getClass());
        }

        for (Field field : filmUpdate.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(filmUpdate);
                if (value != null) {
                    field.set(film, value);
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }

        return film;
    }
}
