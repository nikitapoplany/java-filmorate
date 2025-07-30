package ru.yandex.practicum.filmorate.film;

import java.util.Collection;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;

@RestController
@RequestMapping("/films")
public class FilmController {

    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    protected Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @NotNull @RequestBody FilmCreateDto filmCreateDto) {
        return filmService.create(filmCreateDto);
    }

    @PutMapping
    public Film update(@RequestBody FilmUpdateDto filmUpdateDto) {
        return filmService.update(filmUpdateDto);
    }

}
