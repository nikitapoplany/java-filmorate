package ru.yandex.practicum.filmorate.service;

import java.util.*;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import static ru.yandex.practicum.filmorate.util.Validators.MAX_FILM_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.util.Validators.isValidFilmReleaseDate;

@Service
public class FilmService extends AbstractService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(FilmCreateDto filmCreateDto) {
        return filmStorage.create(filmCreateDto);
    }

    public Film update(FilmUpdateDto filmUpdateDto) {
        if (!filmStorage.getStorage().containsKey(filmUpdateDto.getId())) {
            LoggedException.throwNew(
                    new NotFoundException(String.format("Ошибка при обновлении фильма id=%d: фильм не найден",
                            filmUpdateDto.getId())), getClass());
        }

        Film filmUpdate = FilmMapper.toEntity(filmUpdateDto);
        Film filmOriginal = filmStorage.findById(filmUpdate.getId());

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

        return filmStorage.update(filmUpdate, filmOriginal);
    }

    public void addLike(Integer filmId, Integer userId, @Autowired UserService userService) {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findById(filmId));
        if (filmOptional.isEmpty()) {
            LoggedException.throwNew(
                    new NotFoundException(
                            String.format("Невозможно поставить лайк. Фильм id %d не найден", filmId)),
                    getClass()
            );
        }
        if (Optional.ofNullable(userService.findById(userId)).isEmpty()) {
            LoggedException.throwNew(new NotFoundException(String.format("Невозможно поставить лайк. Пользователь id %d не найден",
                    userId)), getClass());
        }
        filmOptional.get().getLikes().add(userId);
    }

    public void removeLike(Integer filmId, Integer userId, @Autowired UserService userService) {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findById(filmId));
        if (filmOptional.isEmpty()) {
            LoggedException.throwNew(
                    new NotFoundException(String.format("Невозможно убрать лайк. Фильм id %d не найден", filmId)),
                    getClass()
            );
        }
        if (Optional.ofNullable(userService.findById(userId)).isEmpty()) {
            LoggedException.throwNew(new NotFoundException(String.format("Невозможно убрать лайк. Пользователь id %d не найден",
                    userId)), getClass());
        }
        filmOptional.get().getLikes().remove(userId);
    }

    public List<Film> findTopLiked() {
        return filmStorage.findTopLiked(10);
    }
}
