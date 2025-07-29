package ru.yandex.practicum.filmorate.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggedException {

    public static void throwNew(RuntimeException exception, Class<?> clazz) {
        Logger log = LoggerFactory.getLogger(clazz);
        log.error(exception.getMessage(), exception);
        throw exception;
    }
}
