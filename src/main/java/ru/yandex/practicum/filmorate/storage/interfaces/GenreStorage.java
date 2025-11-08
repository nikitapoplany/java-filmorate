package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.ArrayList;
import java.util.Set;

import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {
    Set<Genre> findAll();

    Genre findById(Integer genreId);

    ArrayList<Genre> findGenreByFilmId(Integer filmId);

    void linkGenresToFilm(Integer filmId, Set<Integer> genreIdSet, boolean clearExisting);
}
