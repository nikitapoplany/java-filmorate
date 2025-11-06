package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.util.Validators;
import ru.yandex.practicum.filmorate.util.ValidatorsDb;

@Service
public class GenreService {
    private final GenreDbStorage genreStorage;
    private final ValidatorsDb validatorsDb;

    @Autowired
    public GenreService(GenreDbStorage genreStorage, ValidatorsDb validatorsDb) {
        this.genreStorage = genreStorage;
        this.validatorsDb = validatorsDb;
    }

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

    public ArrayList<Genre> findGenreByFilmId(Integer filmId) {
        return genreStorage.findGenreByFilmId(filmId);
    }

    public void linkGenreToFilm(Integer filmId, Set<Integer> genreSet) {
        for (Integer genreId: genreSet) {
            genreStorage.likeGenreToFilm(filmId, genreId);
        }
    }
}
