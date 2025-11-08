package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    List<Film> findAll();

    Film findById(Integer filmId);

    Film create(Film film);

    Film update(Film filmUpdate);

    Integer delete(Integer filmId);

    List<Film> findTopLiked(int size);
}
