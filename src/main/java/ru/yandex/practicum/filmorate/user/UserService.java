package ru.yandex.practicum.filmorate.user;

import java.lang.reflect.Field;
import java.util.Collection;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.abstraction.AbstractService;
import ru.yandex.practicum.filmorate.dto.user.*;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Service
public class UserService extends AbstractService<User> {

    public Collection<User> findAll() {
        return mapEntityStorage.values();
    }

    public User create(UserCreateDto userCreateDto) {
        User user = UserMapper.toEntity(userCreateDto);
        user.setId(getNextId());
        mapEntityStorage.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    public User update(UserUpdateDto userUpdateDto) {
        User userUpdate = UserMapper.toEntity(userUpdateDto);

        if (!mapEntityStorage.containsKey(userUpdate.getId())) {
            LoggedException.throwNew(
                    new NotFoundException(String.format("Ошибка при обновлении пользователя" +
                            " id=%d: пользователь не найден", userUpdate.getId())), getClass());
        }

        User user = mapEntityStorage.get(userUpdate.getId());

        for (Field field : userUpdate.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(userUpdate);
                if (value != null) {
                    field.set(user, value);
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }

        return user;
    }
}
