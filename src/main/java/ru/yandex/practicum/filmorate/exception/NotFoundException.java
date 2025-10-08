package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, которое выбрасывается, когда запрашиваемый ресурс не найден
 */
public class NotFoundException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением
     *
     * @param message сообщение об ошибке
     */
    public NotFoundException(String message) {
        super(message);
    }
}