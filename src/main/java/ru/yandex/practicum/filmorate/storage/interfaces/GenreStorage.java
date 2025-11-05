package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.Set;

import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {
    Set<Genre> findAll();

    Genre findById(Integer genreId);
}
