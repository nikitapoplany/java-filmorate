package ru.yandex.practicum.filmorate.storage;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class InMemoryFilmStorage extends AbstractStorage<Film> implements FilmStorage {
    @Override
    public Map<Integer, Film> getStorage() {
        return Map.copyOf(mapEntityStorage);
    }

    @Override
    public Collection<Film> findAll() {
        return mapEntityStorage.values();
    }

    @Override
    public Film findById(Integer filmId) {
        return mapEntityStorage.get(filmId);
    }

    @Override
    public Film create(FilmCreateDto filmCreateDto) {
        Film film = FilmMapper.toEntity(filmCreateDto);
        film.setId(getNextId());
        mapEntityStorage.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film filmUpdate, Film filmOriginal) {
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

        return filmOriginal;
    }

    @Override
    public Integer delete(Integer filmId) {
        mapEntityStorage.remove(filmId);
        return filmId;
    }
}
