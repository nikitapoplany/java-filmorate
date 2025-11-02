package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с фильмами
 */
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                      @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    /**
     * Получение списка всех фильмов
     *
     * @return список фильмов
     */
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    /**
     * Добавление нового фильма
     *
     * @param film данные фильма
     * @return добавленный фильм
     */
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    /**
     * Обновление существующего фильма
     *
     * @param film данные фильма
     * @return обновленный фильм
     */
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    /**
     * Получение фильма по идентификатору
     *
     * @param id идентификатор фильма
     * @return фильм
     * @throws NotFoundException если фильм не найден
     */
    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    /**
     * Добавление лайка фильму
     *
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     * @return фильм с обновленным списком лайков
     * @throws NotFoundException если фильм или пользователь не найден
     */
    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        // Проверяем, существует ли пользователь
        if (!userStorage.userExists(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        film.addLike(userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
        return film;
    }

    /**
     * Удаление лайка у фильма
     *
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     * @return фильм с обновленным списком лайков
     * @throws NotFoundException если фильм или пользователь не найден
     */
    public Film removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        // Проверяем, существует ли пользователь
        if (!userStorage.userExists(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (!film.removeLike(userId)) {
            log.warn("Пользователь с id {} не ставил лайк фильму с id {}", userId, filmId);
            throw new NotFoundException("Пользователь с id " + userId + " не ставил лайк фильму с id " + filmId);
        }

        log.info("Пользователь с id {} удалил лайк у фильма с id {}", userId, filmId);
        return film;
    }

    /**
     * Получение списка популярных фильмов
     *
     * @param count количество фильмов
     * @return список популярных фильмов
     */
    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesCount(), f1.getLikesCount()))
                .limit(count)
                .collect(Collectors.toList());

        log.info("Получен список популярных фильмов. Количество: {}", popularFilms.size());
        return popularFilms;
    }
}