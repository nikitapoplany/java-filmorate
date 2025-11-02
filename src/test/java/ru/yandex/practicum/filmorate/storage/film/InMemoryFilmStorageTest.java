package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для хранилища фильмов в памяти
 */
class InMemoryFilmStorageTest {

    private InMemoryFilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
    }

    /**
     * Тест проверяет добавление фильма
     */
    @Test
    void shouldAddFilm() {
        // Подготовка данных
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        // Вызов тестируемого метода
        Film addedFilm = filmStorage.addFilm(film);

        // Проверка результатов
        assertNotNull(addedFilm);
        assertEquals(1, addedFilm.getId());
        assertEquals("Название фильма", addedFilm.getName());
        assertEquals("Описание фильма", addedFilm.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), addedFilm.getReleaseDate());
        assertEquals(120, addedFilm.getDuration());
    }

    /**
     * Тест проверяет, что при добавлении фильма с пустым названием выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenAddingFilmWithEmptyName() {
        // Подготовка данных
        Film film = new Film();
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        // Проверка исключения
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Название фильма не может быть пустым"));
    }

    /**
     * Тест проверяет, что при добавлении фильма с описанием длиннее 200 символов выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenAddingFilmWithTooLongDescription() {
        // Подготовка данных
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("A".repeat(201)); // Описание длиной 201 символ
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        // Проверка исключения
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Описание фильма не может быть длиннее 200 символов"));
    }

    /**
     * Тест проверяет, что при добавлении фильма с датой релиза раньше минимальной выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenAddingFilmWithTooEarlyReleaseDate() {
        // Подготовка данных
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // На день раньше минимальной даты
        film.setDuration(120);

        // Проверка исключения
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }

    /**
     * Тест проверяет, что при добавлении фильма с неположительной продолжительностью выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenAddingFilmWithNonPositiveDuration() {
        // Подготовка данных
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        // Проверка исключения
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Продолжительность фильма должна быть положительной"));
    }

    /**
     * Тест проверяет обновление фильма
     */
    @Test
    void shouldUpdateFilm() {
        // Подготовка данных - сначала добавляем фильм
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Film addedFilm = filmStorage.addFilm(film);

        // Подготовка данных для обновления
        Film updatedFilm = new Film();
        updatedFilm.setId(addedFilm.getId());
        updatedFilm.setName("Новое название");
        updatedFilm.setDescription("Новое описание");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 2, 2));
        updatedFilm.setDuration(150);

        // Вызов тестируемого метода
        Film result = filmStorage.updateFilm(updatedFilm);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(addedFilm.getId(), result.getId());
        assertEquals("Новое название", result.getName());
        assertEquals("Новое описание", result.getDescription());
        assertEquals(LocalDate.of(2001, 2, 2), result.getReleaseDate());
        assertEquals(150, result.getDuration());
    }

    /**
     * Тест проверяет, что при обновлении несуществующего фильма выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentFilm() {
        // Подготовка данных
        Film film = new Film();
        film.setId(999); // Несуществующий ID
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        // Проверка исключения
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmStorage.updateFilm(film)
        );

        assertTrue(exception.getMessage().contains("Фильм с id 999 не найден"));
    }

    /**
     * Тест проверяет получение всех фильмов
     */
    @Test
    void shouldGetAllFilms() {
        // Подготовка данных - добавляем два фильма
        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание фильма 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        filmStorage.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание фильма 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);
        filmStorage.addFilm(film2);

        // Вызов тестируемого метода
        List<Film> films = filmStorage.getAllFilms();

        // Проверка результатов
        assertNotNull(films);
        assertEquals(2, films.size());
        assertEquals("Фильм 1", films.get(0).getName());
        assertEquals("Фильм 2", films.get(1).getName());
    }

    /**
     * Тест проверяет получение фильма по идентификатору
     */
    @Test
    void shouldGetFilmById() {
        // Подготовка данных - добавляем фильм
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Film addedFilm = filmStorage.addFilm(film);

        // Вызов тестируемого метода
        Optional<Film> retrievedFilmOptional = filmStorage.getFilmById(addedFilm.getId());

        // Проверка результатов
        assertTrue(retrievedFilmOptional.isPresent());
        Film retrievedFilm = retrievedFilmOptional.get();
        assertEquals(addedFilm.getId(), retrievedFilm.getId());
        assertEquals("Название фильма", retrievedFilm.getName());
        assertEquals("Описание фильма", retrievedFilm.getDescription());
    }

    /**
     * Тест проверяет, что при получении несуществующего фильма возвращается пустой Optional
     */
    @Test
    void shouldReturnEmptyOptionalWhenGettingNonExistentFilm() {
        // Вызов тестируемого метода
        Optional<Film> filmOptional = filmStorage.getFilmById(999);

        // Проверка результатов
        assertTrue(filmOptional.isEmpty());
    }

    /**
     * Тест проверяет проверку существования фильма
     */
    @Test
    void shouldCheckFilmExists() {
        // Подготовка данных - добавляем фильм
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Film addedFilm = filmStorage.addFilm(film);

        // Проверка результатов
        assertTrue(filmStorage.filmExists(addedFilm.getId()));
        assertFalse(filmStorage.filmExists(999));
    }
}