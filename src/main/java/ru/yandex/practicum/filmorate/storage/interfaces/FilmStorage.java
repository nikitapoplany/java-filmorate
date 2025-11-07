package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;

import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    List<Film> findAll();

    Film findById(Integer filmId);

    Film create(FilmCreateDto filmCreateDto);

    Film update(Film filmUpdate, Film filmOriginal);

    Integer delete(Integer filmId);

    List<Film> findTopLiked(int size);
}
