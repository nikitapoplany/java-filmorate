package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Обработчик исключений для контроллеров
 */
@RestControllerAdvice
public class ErrorHandler {
    private static final String ERROR_KEY = "error";

    /**
     * Обрабатывает исключения валидации
     *
     * @param e исключение валидации
     * @return сообщение об ошибке
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        return Map.of(ERROR_KEY, e.getMessage());
    }

    /**
     * Обрабатывает исключения валидации аннотаций
     *
     * @param e исключение валидации аннотаций
     * @return сообщения об ошибках
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationAnnotationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Обрабатывает исключения "не найдено"
     *
     * @param e исключение "не найдено"
     * @return сообщение об ошибке
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of(ERROR_KEY, e.getMessage());
    }

    /**
     * Обрабатывает все остальные исключения
     *
     * @param e исключение
     * @return сообщение об ошибке
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(final Throwable e) {
        return Map.of(ERROR_KEY, "Произошла непредвиденная ошибка.");
    }
}