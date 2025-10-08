package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

/**
 * Контроллер для работы с фильмами
 */
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Получение списка всех фильмов
     *
     * @return список фильмов
     */
    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получен запрос на получение всех фильмов");
        return filmService.getAllFilms();
    }

    /**
     * Получение фильма по идентификатору
     *
     * @param id идентификатор фильма
     * @return фильм
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("Получен запрос на получение фильма с id: {}", id);
        return filmService.getFilmById(id);
    }

    /**
     * Добавление нового фильма
     *
     * @param film данные фильма
     * @return добавленный фильм
     */
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма: {}", film);
        return filmService.addFilm(film);
    }

    /**
     * Обновление существующего фильма
     *
     * @param film данные фильма
     * @return обновленный фильм
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма: {}", film);
        return filmService.updateFilm(film);
    }

    /**
     * Добавление лайка фильму
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     * @return фильм с обновленным списком лайков
     */
    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на добавление лайка фильму с id {} от пользователя с id {}", id, userId);
        return filmService.addLike(id, userId);
    }

    /**
     * Удаление лайка у фильма
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     * @return фильм с обновленным списком лайков
     */
    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на удаление лайка у фильма с id {} от пользователя с id {}", id, userId);
        return filmService.removeLike(id, userId);
    }

    /**
     * Получение списка популярных фильмов
     *
     * @param count количество фильмов (по умолчанию 10)
     * @return список популярных фильмов
     */
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение {} популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}
