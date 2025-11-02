package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервиса фильмов
 */
class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест проверяет получение всех фильмов
     */
    @Test
    void shouldGetAllFilms() {
        // Подготовка данных
        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Фильм 1");
        film1.setDescription("Описание фильма 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);

        Film film2 = new Film();
        film2.setId(2);
        film2.setName("Фильм 2");
        film2.setDescription("Описание фильма 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);

        List<Film> expectedFilms = List.of(film1, film2);

        // Настройка мока
        when(filmStorage.getAllFilms()).thenReturn(expectedFilms);

        // Вызов тестируемого метода
        List<Film> films = filmService.getAllFilms();

        // Проверка результатов
        assertNotNull(films);
        assertEquals(2, films.size());
        assertEquals(1, films.get(0).getId());
        assertEquals(2, films.get(1).getId());

        // Проверка вызова метода хранилища
        verify(filmStorage, times(1)).getAllFilms();
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

        Film expectedFilm = new Film();
        expectedFilm.setId(1);
        expectedFilm.setName("Название фильма");
        expectedFilm.setDescription("Описание фильма");
        expectedFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        expectedFilm.setDuration(120);

        // Настройка мока
        when(filmStorage.addFilm(any(Film.class))).thenReturn(expectedFilm);

        // Вызов тестируемого метода
        Film addedFilm = filmService.addFilm(film);

        // Проверка результатов
        assertNotNull(addedFilm);
        assertEquals(1, addedFilm.getId());
        assertEquals("Название фильма", addedFilm.getName());

        // Проверка вызова метода хранилища
        verify(filmStorage, times(1)).addFilm(film);
    }

    /**
     * Тест проверяет обновление фильма
     */
    @Test
    void shouldUpdateFilm() {
        // Подготовка данных
        Film film = new Film();
        film.setId(1);
        film.setName("Новое название");
        film.setDescription("Новое описание");
        film.setReleaseDate(LocalDate.of(2001, 2, 2));
        film.setDuration(150);

        // Настройка мока
        when(filmStorage.updateFilm(any(Film.class))).thenReturn(film);

        // Вызов тестируемого метода
        Film updatedFilm = filmService.updateFilm(film);

        // Проверка результатов
        assertNotNull(updatedFilm);
        assertEquals(1, updatedFilm.getId());
        assertEquals("Новое название", updatedFilm.getName());
        assertEquals("Новое описание", updatedFilm.getDescription());

        // Проверка вызова метода хранилища
        verify(filmStorage, times(1)).updateFilm(film);
    }

    /**
     * Тест проверяет получение фильма по идентификатору
     */
    @Test
    void shouldGetFilmById() {
        // Подготовка данных
        int filmId = 1;
        Film expectedFilm = new Film();
        expectedFilm.setId(filmId);
        expectedFilm.setName("Название фильма");
        expectedFilm.setDescription("Описание фильма");
        expectedFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        expectedFilm.setDuration(120);

        // Настройка мока
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(expectedFilm));

        // Вызов тестируемого метода
        Film film = filmService.getFilmById(filmId);

        // Проверка результатов
        assertNotNull(film);
        assertEquals(filmId, film.getId());
        assertEquals("Название фильма", film.getName());

        // Проверка вызова метода хранилища
        verify(filmStorage, times(1)).getFilmById(filmId);
    }

    /**
     * Тест проверяет добавление лайка фильму
     */
    @Test
    void shouldAddLike() {
        // Подготовка данных
        int filmId = 1;
        int userId = 1;

        Film film = new Film();
        film.setId(filmId);
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        // Настройка моков
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userStorage.userExists(userId)).thenReturn(true);

        // Вызов тестируемого метода
        Film updatedFilm = filmService.addLike(filmId, userId);

        // Проверка результатов
        assertNotNull(updatedFilm);
        assertEquals(filmId, updatedFilm.getId());
        assertTrue(updatedFilm.getLikes().contains(userId));

        // Проверка вызова методов хранилищ
        verify(filmStorage, times(1)).getFilmById(filmId);
        verify(userStorage, times(1)).userExists(userId);
    }

    /**
     * Тест проверяет, что при добавлении лайка несуществующему фильму выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenAddingLikeToNonExistentFilm() {
        // Подготовка данных
        int filmId = 999;
        int userId = 1;

        // Настройка мока
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.empty());

        // Проверка исключения
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmService.addLike(filmId, userId)
        );

        assertTrue(exception.getMessage().contains("Фильм с id 999 не найден"));

        // Проверка вызова метода хранилища
        verify(filmStorage, times(1)).getFilmById(filmId);
        verify(userStorage, never()).userExists(anyInt());
    }

    /**
     * Тест проверяет, что при добавлении лайка от несуществующего пользователя выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenAddingLikeFromNonExistentUser() {
        // Подготовка данных
        int filmId = 1;
        int userId = 999;

        Film film = new Film();
        film.setId(filmId);
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        // Настройка моков
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userStorage.userExists(userId)).thenReturn(false);

        // Проверка исключения
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmService.addLike(filmId, userId)
        );

        assertTrue(exception.getMessage().contains("Пользователь с id 999 не найден"));

        // Проверка вызова методов хранилищ
        verify(filmStorage, times(1)).getFilmById(filmId);
        verify(userStorage, times(1)).userExists(userId);
    }

    /**
     * Тест проверяет удаление лайка у фильма
     */
    @Test
    void shouldRemoveLike() {
        // Подготовка данных
        int filmId = 1;
        int userId = 1;

        Film film = new Film();
        film.setId(filmId);
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.addLike(userId);

        // Настройка моков
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userStorage.userExists(userId)).thenReturn(true);

        // Вызов тестируемого метода
        Film updatedFilm = filmService.removeLike(filmId, userId);

        // Проверка результатов
        assertNotNull(updatedFilm);
        assertEquals(filmId, updatedFilm.getId());
        assertFalse(updatedFilm.getLikes().contains(userId));

        // Проверка вызова методов хранилищ
        verify(filmStorage, times(1)).getFilmById(filmId);
        verify(userStorage, times(1)).userExists(userId);
    }

    /**
     * Тест проверяет получение популярных фильмов
     */
    @Test
    void shouldGetPopularFilms() {
        // Подготовка данных
        int count = 10;

        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Фильм 1");
        film1.setDescription("Описание фильма 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.addLike(1);
        film1.addLike(2);

        Film film2 = new Film();
        film2.setId(2);
        film2.setName("Фильм 2");
        film2.setDescription("Описание фильма 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);
        film2.addLike(1);

        List<Film> popularFilms = List.of(film1, film2);

        // Настройка мока
        when(filmStorage.getPopularFilms(count)).thenReturn(popularFilms);

        // Вызов тестируемого метода
        List<Film> result = filmService.getPopularFilms(count);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId()); // Фильм с 2 лайками должен быть первым (сортировка по количеству лайков в порядке убывания)
        assertEquals(2, result.get(1).getId()); // Фильм с 1 лайком должен быть вторым (сортировка по количеству лайков в порядке убывания)

        // Проверка вызова метода хранилища
        verify(filmStorage, times(1)).getPopularFilms(count);
    }
}