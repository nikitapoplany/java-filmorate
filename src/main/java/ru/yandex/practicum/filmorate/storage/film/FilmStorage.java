package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/**
 * Интерфейс хранилища фильмов
 */
public interface FilmStorage {
    /**
     * Получение списка всех фильмов
     *
     * @return список фильмов
     */
    List<Film> getAllFilms();

    /**
     * Добавление нового фильма
     *
     * @param film данные фильма
     * @return добавленный фильм
     */
    Film addFilm(Film film);

    /**
     * Обновление существующего фильма
     *
     * @param film данные фильма
     * @return обновленный фильм
     */
    Film updateFilm(Film film);

    /**
     * Получение фильма по идентификатору
     *
     * @param id идентификатор фильма
     * @return фильм
     */
    Film getFilmById(int id);

    /**
     * Проверка существования фильма
     *
     * @param id идентификатор фильма
     * @return true, если фильм существует, иначе false
     */
    boolean filmExists(int id);
}