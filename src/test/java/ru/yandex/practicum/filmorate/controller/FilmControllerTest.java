package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddFilmWithValidData() {
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

        when(filmService.addFilm(any(Film.class))).thenReturn(expectedFilm);

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals(1, addedFilm.getId());
        assertEquals("Название фильма", addedFilm.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        Film film = new Film();
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        when(filmService.addFilm(any(Film.class))).thenThrow(
                new ValidationException("Название фильма не может быть пустым")
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Название фильма не может быть пустым"));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        Film film = new Film();
        film.setName("Название фильма");
        // Создаем описание длиной 201 символ
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Описание фильма не может быть длиннее 200 символов"));
    }

    @Test
    void shouldAcceptDescriptionWithExactly200Chars() {
        Film film = new Film();
        film.setName("Название фильма");
        // Создаем описание длиной 200 символов
        film.setDescription("A".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals(200, addedFilm.getDescription().length());
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsTooEarly() {
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // На день раньше минимальной даты
        film.setDuration(120);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }

    @Test
    void shouldAcceptMinimumReleaseDate() {
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1895, 12, 28)); // Минимальная дата
        film.setDuration(120);

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals(LocalDate.of(1895, 12, 28), addedFilm.getReleaseDate());
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-1);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Продолжительность фильма должна быть положительной"));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsZero() {
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Продолжительность фильма должна быть положительной"));
    }

    @Test
    void shouldUpdateExistingFilm() {
        // Сначала добавляем фильм
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Film addedFilm = filmController.addFilm(film);

        // Затем обновляем его
        Film updatedFilm = new Film();
        updatedFilm.setId(addedFilm.getId());
        updatedFilm.setName("Новое название");
        updatedFilm.setDescription("Новое описание");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 2, 2));
        updatedFilm.setDuration(150);

        Film result = filmController.updateFilm(updatedFilm);

        assertNotNull(result);
        assertEquals(addedFilm.getId(), result.getId());
        assertEquals("Новое название", result.getName());
        assertEquals("Новое описание", result.getDescription());
        assertEquals(LocalDate.of(2001, 2, 2), result.getReleaseDate());
        assertEquals(150, result.getDuration());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentFilm() {
        Film film = new Film();
        film.setId(999); // Несуществующий ID
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmController.updateFilm(film)
        );

        assertTrue(exception.getMessage().contains("Фильм с id 999 не найден"));
    }
}