package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    /**
     * Получение списка всех фильмов
     *
     * @return список фильмов
     */
    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Получен запрос на получение всех фильмов");
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    /**
     * Получение фильма по идентификатору
     *
     * @param id идентификатор фильма
     * @return фильм
     */
    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable int id) {
        log.info("Получен запрос на получение фильма с id: {}", id);
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    /**
     * Добавление нового фильма
     *
     * @param film данные фильма
     * @return добавленный фильм
     */
    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма: {}", film);
        return ResponseEntity.ok(filmService.addFilm(film));
    }

    /**
     * Обновление существующего фильма
     *
     * @param film данные фильма
     * @return обновленный фильм
     */
    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма: {}", film);
        return ResponseEntity.ok(filmService.updateFilm(film));
    }

    /**
     * Добавление лайка фильму
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     * @return фильм с обновленным списком лайков
     */
    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на добавление лайка фильму с id {} от пользователя с id {}", id, userId);
        return ResponseEntity.ok(filmService.addLike(id, userId));
    }

    /**
     * Удаление лайка у фильма
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     * @return фильм с обновленным списком лайков
     */
    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на удаление лайка у фильма с id {} от пользователя с id {}", id, userId);
        return ResponseEntity.ok(filmService.removeLike(id, userId));
    }

    /**
     * Получение списка популярных фильмов
     *
     * @param count количество фильмов (по умолчанию 10)
     * @return список популярных фильмов
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение {} популярных фильмов", count);
        return ResponseEntity.ok(filmService.getPopularFilms(count));
    }
}
