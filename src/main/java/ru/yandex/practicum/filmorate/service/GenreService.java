package ru.yandex.practicum.filmorate.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

@Service
public class GenreService {
    private final GenreDbStorage genreStorage;

    @Autowired
    public GenreService(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Set<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre findById(Integer mpaId) {
        return genreStorage.findById(mpaId);
    }
}
