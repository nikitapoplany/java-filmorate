package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Mpa;

public interface MpaStorage {
    List<Mpa> findAll();

    Mpa findById(Integer mpaId);
}