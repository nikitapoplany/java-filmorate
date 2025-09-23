package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасываемое при ошибках валидации
 */
public class ValidationException extends RuntimeException {
    
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     * 
     * @param message сообщение об ошибке
     */
    public ValidationException(String message) {
        super(message);
    }
}