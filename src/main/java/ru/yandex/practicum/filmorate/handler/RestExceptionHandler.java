package ru.yandex.practicum.filmorate.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(String.format("An error occurred while handling request:%n %s",
                e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}