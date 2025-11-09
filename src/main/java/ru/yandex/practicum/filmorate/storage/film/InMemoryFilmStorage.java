package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация хранилища фильмов в памяти
 */
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @Override
    public List<Film> getAllFilms() {
        log.debug("Получение списка всех фильмов. Количество: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> {
                    // Сначала сортируем по количеству лайков (по убыванию)
                    int likesCompare = Integer.compare(f2.getLikesCount(), f1.getLikesCount());
                    if (likesCompare != 0) {
                        return likesCompare;
                    }
                    // При равном количестве лайков сортируем по id (по убыванию)
                    return Integer.compare(f2.getId(), f1.getId());
                })
                .limit(count)
                .collect(Collectors.toList());
    }


    @Override
    public Film addFilm(Film film) {
        log.debug("Добавление фильма: {}", film);
        validateFilm(film);
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.debug("Фильм успешно добавлен: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Обновление фильма: {}", film);
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с id {} не найден", film.getId());
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.debug("Фильм успешно обновлен: {}", film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        log.debug("Получение фильма по id: {}", id);
        if (!films.containsKey(id)) {
            log.warn("Фильм с id {} не найден", id);
            return Optional.empty();
        }
        return Optional.of(films.get(id));
    }

    @Override
    public boolean filmExists(int id) {
        return films.containsKey(id);
    }

    /**
     * Валидация данных фильма
     *
     * @param film данные фильма
     * @throws ValidationException если данные не прошли валидацию
     */
    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Описание фильма не может быть длиннее 200 символов");
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}