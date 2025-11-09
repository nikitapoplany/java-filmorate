package ru.yandex.practicum.filmorate.service;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.util.ValidatorsDb;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreStorage;
    private final ValidatorsDb validatorsDb;

    public Set<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre findById(Integer genreId) {
        if (!validatorsDb.isValidGenre(genreId)) {
            LoggedException.throwNew(
                    new NotFoundException(String.format("Жанр id %d не найден.", genreId)), getClass()
            );
        }
        return genreStorage.findById(genreId);
    }

    public List<Genre> findGenreByFilmId(Integer filmId) {
        return genreStorage.findGenreByFilmId(filmId);
    }

    public void linkGenresToFilm(Integer filmId, Set<Integer> genreIdSet, boolean clearExisting) {
        genreStorage.linkGenresToFilm(filmId, genreIdSet, clearExisting);
    }
}
