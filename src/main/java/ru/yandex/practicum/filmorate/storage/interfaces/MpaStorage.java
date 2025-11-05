package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.Set;

import ru.yandex.practicum.filmorate.model.Mpa;

public interface MpaStorage {
    Set<Mpa> findAll();

    Mpa findById(Integer mpaId);

}