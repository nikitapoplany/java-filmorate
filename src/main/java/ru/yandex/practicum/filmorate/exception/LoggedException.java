package ru.yandex.practicum.filmorate.exception;

import java.util.List;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggedException {

    public static void throwNew(RuntimeException exception, Class<?> clazz) {
        Logger log = LoggerFactory.getLogger(clazz);
        log.error(exception.getMessage(), exception);
        throw exception;
    }

    public static void throwNew(ExceptionType exceptionType, Class<?> clazz, List<Integer> id) {
        Logger log = LoggerFactory.getLogger(clazz);
        RuntimeException exception;
        switch (exceptionType) {
            case INVALID_LOGIN -> {
                exception = new ValidationException("Логин не должен содержать пробелы или быть пустым");
            }
            case USER_NOT_FOUND -> {
                exception = new NotFoundException(String.format("Пользователь id=%d не найден.", id.getFirst()));
            }
            case FILM_NOT_FOUND -> {
                exception = new NotFoundException(String.format("Фильм id=%d не найден.", id.getFirst()));
            }
            case MPA_NOT_FOUND -> {
                exception = new NotFoundException(String.format("MPA id=%d не найден.", id.getFirst()));
            }
            case GENRE_NOT_FOUND -> {
                exception = new NotFoundException(String.format("Жанр id=%d не найден.", id.getFirst()));
            }
            case INVALID_FRIENDSHIP -> {
                exception = new NotFoundException(String.format("Не удалось добавить друга с id=%d пользователю id=%d."
                                + "Убедитесь, что id пользователей указаны верно.", id.get(0), id.get(1)));
            }
            case USER_LIKE_ALREADY_EXISTS -> {
                exception = new ValidationException(
                        String.format("Пользователь id=%d уже поставил лайк фильму id=%d", id.get(0), id.get(1))
                );
            }
            case USER_LIKE_NOT_EXISTS -> {
                exception = new NotFoundException(
                        String.format("Ошибка при удалении лайка. Пользователь id=%d не ставил лайк фильму id=%d.",
                                id.get(0), id.get(1))
                );
            }
            case INVALID_FILM_RELEASE_DATE -> {
                exception = new ValidationException(
                        String.format("Дата создания фильма не может быть ранее 28 декабря 1895 г."
                                + " Ошибка обновления фильма id=%d.", id.getFirst()));
            }
            default -> exception = new RuntimeException("Произошла непредвиденная ошибка при обработке запроса.");
        }
        log.error(exception.getMessage(), exception);
        throw exception;
    }
}
