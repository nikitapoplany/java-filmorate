package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс хранилища жанров
 */
public interface GenreStorage {
    /**
     * Получение списка всех жанров
     *
     * @return список жанров
     */
    List<Genre> getAllGenres();

    /**
     * Получение жанра по идентификатору
     *
     * @param id идентификатор жанра
     * @return Optional, содержащий жанр, или пустой Optional, если жанр не найден
     */
    Optional<Genre> getGenreById(int id);

    /**
     * Получение списка жанров для фильма
     *
     * @param filmId идентификатор фильма
     * @return список жанров фильма
     */
    List<Genre> getGenresByFilmId(int filmId);

    /**
     * Добавление жанров для фильма
     *
     * @param filmId идентификатор фильма
     * @param genres список жанров
     */
    void addGenresToFilm(int filmId, List<Genre> genres);

    /**
     * Удаление всех жанров у фильма
     *
     * @param filmId идентификатор фильма
     */
    void deleteGenresFromFilm(int filmId);
}