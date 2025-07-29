package ru.yandex.practicum.filmorate.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Void> handleException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        // ResponseEntity.status(HttpStatus.BAD_REQUEST).body()
        return null;
    }
}

// org.springframework.web.method.annotation.HandlerMethodValidationException: 400 BAD_REQUEST "Validation failure"