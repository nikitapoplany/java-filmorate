package ru.yandex.practicum.filmorate.storage;

import java.lang.reflect.Field;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

@Component
public class InMemoryFilmStorage extends AbstractStorage<Film> implements FilmStorage {
    @Autowired
    private FilmMapper filmMapper;

    public Map<Integer, Film> getStorage() {
        return Map.copyOf(mapEntityStorage);
    }

    @Override
    public Collection<Film> findAll() {
        return mapEntityStorage.values();
    }

    @Override
    public Film findById(Integer filmId) {
        if (!mapEntityStorage.containsKey(filmId)) {
            LoggedException.throwNew(new NotFoundException(String.format("Фильм id %d не найден", filmId)), getClass());
        }
        return mapEntityStorage.get(filmId);
    }

    @Override
    public Film create(FilmCreateDto filmCreateDto) {
        Film film = filmMapper.toEntity(filmCreateDto);
        film.setId(getNextId());
        mapEntityStorage.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film filmUpdate, Film filmOriginal) {
        String copy = filmOriginal.toString();

        for (Field field : filmUpdate.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(filmUpdate);
                if (value != null) {
                    field.set(filmOriginal, value);
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }

        log.info("Обновлён фильм {}. Новое значение: {}", copy, filmOriginal);
        return filmOriginal;
    }

    @Override
    public Integer delete(Integer filmId) {
        mapEntityStorage.remove(filmId);
        log.info("Удалён фильм id {}", filmId);
        return filmId;
    }

    @Override
    public List<Film> findTopLiked(int count) {
        return mapEntityStorage.values().stream()
                .sorted(Comparator.<Film, Integer>comparing(film -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}
