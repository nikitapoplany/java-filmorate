package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

/**
 * Контроллер для работы с жанрами
 */
@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    /**
     * Получение списка всех жанров
     *
     * @return список жанров
     */
    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Получен запрос GET /genres");
        return genreService.getAllGenres();
    }

    /**
     * Получение жанра по идентификатору
     *
     * @param id идентификатор жанра
     * @return жанр
     */
    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.info("Получен запрос GET /genres/{}", id);
        return genreService.getGenreById(id);
    }
}