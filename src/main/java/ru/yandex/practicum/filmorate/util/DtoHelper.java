package ru.yandex.practicum.filmorate.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class DtoHelper {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public Object transferFields(Object from, Object to) {
        for (Field field : from.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(to);
                if (value == null) {
                    field.set(to, field.get(from));
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }

        return to;
    }
}
