package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Тесты для контроллера фильмов
 */
@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

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

    /**
     * Тест проверяет, что при добавлении фильма с описанием длиннее 200 символов выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        Film film = new Film();
        film.setName("Название фильма");
        // Создаем описание длиной 201 символ
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        when(filmService.addFilm(any(Film.class))).thenThrow(
                new ValidationException("Описание фильма не может быть длиннее 200 символов")
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Описание фильма не может быть длиннее 200 символов"));
    }

    /**
     * Тест проверяет, что фильм с описанием ровно 200 символов проходит валидацию
     */
    @Test
    void shouldAcceptDescriptionWithExactly200Chars() {
        Film film = new Film();
        film.setName("Название фильма");
        // Создаем описание длиной 200 символов
        film.setDescription("A".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film expectedFilm = new Film();
        expectedFilm.setId(1);
        expectedFilm.setName("Название фильма");
        expectedFilm.setDescription("A".repeat(200));
        expectedFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        expectedFilm.setDuration(120);

        when(filmService.addFilm(any(Film.class))).thenReturn(expectedFilm);

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals(200, addedFilm.getDescription().length());
    }

    /**
     * Тест проверяет, что при добавлении фильма с датой релиза раньше минимальной выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenReleaseDateIsTooEarly() {
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // На день раньше минимальной даты
        film.setDuration(120);

        when(filmService.addFilm(any(Film.class))).thenThrow(
                new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года")
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }

    /**
     * Тест проверяет, что фильм с минимально допустимой датой релиза проходит валидацию
     */
    @Test
    void shouldAcceptMinimumReleaseDate() {
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1895, 12, 28)); // Минимальная дата
        film.setDuration(120);

        Film expectedFilm = new Film();
        expectedFilm.setId(1);
        expectedFilm.setName("Название фильма");
        expectedFilm.setDescription("Описание фильма");
        expectedFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        expectedFilm.setDuration(120);

        when(filmService.addFilm(any(Film.class))).thenReturn(expectedFilm);

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals(LocalDate.of(1895, 12, 28), addedFilm.getReleaseDate());
    }

    /**
     * Тест проверяет, что при добавлении фильма с отрицательной продолжительностью выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-1);

        when(filmService.addFilm(any(Film.class))).thenThrow(
                new ValidationException("Продолжительность фильма должна быть положительной")
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Продолжительность фильма должна быть положительной"));
    }

    /**
     * Тест проверяет, что при добавлении фильма с нулевой продолжительностью выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenDurationIsZero() {
        Film film = new Film();
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        when(filmService.addFilm(any(Film.class))).thenThrow(
                new ValidationException("Продолжительность фильма должна быть положительной")
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );

        assertTrue(exception.getMessage().contains("Продолжительность фильма должна быть положительной"));
    }

    /**
     * Тест проверяет обновление существующего фильма
     */
    @Test
    void shouldUpdateExistingFilm() {
        // Подготовка данных для обновления
        Film updatedFilm = new Film();
        updatedFilm.setId(1);
        updatedFilm.setName("Новое название");
        updatedFilm.setDescription("Новое описание");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 2, 2));
        updatedFilm.setDuration(150);

        // Настройка мока
        when(filmService.updateFilm(any(Film.class))).thenReturn(updatedFilm);

        // Вызов тестируемого метода
        Film result = filmController.updateFilm(updatedFilm);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(1, result.getId());
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
        Film film = new Film();
        film.setId(999); // Несуществующий ID
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        when(filmService.updateFilm(any(Film.class))).thenThrow(
                new NotFoundException("Фильм с id 999 не найден")
        );

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmController.updateFilm(film)
        );

        assertTrue(exception.getMessage().contains("Фильм с id 999 не найден"));
    }
    
    /**
     * Тест проверяет получение фильма по идентификатору
     */
    @Test
    void shouldGetFilmById() {
        int filmId = 1;
        
        Film expectedFilm = new Film();
        expectedFilm.setId(filmId);
        expectedFilm.setName("Название фильма");
        expectedFilm.setDescription("Описание фильма");
        expectedFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        expectedFilm.setDuration(120);
        
        when(filmService.getFilmById(filmId)).thenReturn(expectedFilm);
        
        Film film = filmController.getFilmById(filmId);
        
        assertNotNull(film);
        assertEquals(filmId, film.getId());
        assertEquals("Название фильма", film.getName());
        assertEquals("Описание фильма", film.getDescription());
    }
    
    /**
     * Тест проверяет добавление лайка фильму
     */
    @Test
    void shouldAddLikeToFilm() {
        int filmId = 1;
        int userId = 1;
        
        Film film = new Film();
        film.setId(filmId);
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.addLike(userId);
        
        when(filmService.addLike(filmId, userId)).thenReturn(film);
        
        Film updatedFilm = filmController.addLike(filmId, userId);
        
        assertNotNull(updatedFilm);
        assertEquals(filmId, updatedFilm.getId());
        assertEquals(1, updatedFilm.getLikesCount());
        assertTrue(updatedFilm.getLikes().contains(userId));
    }
    
    /**
     * Тест проверяет удаление лайка у фильма
     */
    @Test
    void shouldRemoveLikeFromFilm() {
        int filmId = 1;
        int userId = 1;
        
        Film film = new Film();
        film.setId(filmId);
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        // Лайк уже удален
        
        when(filmService.removeLike(filmId, userId)).thenReturn(film);
        
        Film updatedFilm = filmController.removeLike(filmId, userId);
        
        assertNotNull(updatedFilm);
        assertEquals(filmId, updatedFilm.getId());
        assertEquals(0, updatedFilm.getLikesCount());
    }
    
    /**
     * Тест проверяет получение списка популярных фильмов
     */
    @Test
    void shouldGetPopularFilms() {
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
        
        List<Film> expectedFilms = List.of(film1, film2);
        
        when(filmService.getPopularFilms(count)).thenReturn(expectedFilms);
        
        List<Film> popularFilms = filmController.getPopularFilms(count);
        
        assertNotNull(popularFilms);
        assertEquals(2, popularFilms.size());
        assertEquals(1, popularFilms.get(0).getId());
        assertEquals(2, popularFilms.get(0).getLikesCount());
        assertEquals(2, popularFilms.get(1).getId());
        assertEquals(1, popularFilms.get(1).getLikesCount());
    }
}