package ru.yandex.practicum.filmorate.storage;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.util.Validators;

@Component
public class InMemoryUserStorage extends AbstractStorage<User> implements UserStorage {
    public Map<Integer, User> getStorage() {
        return Map.copyOf(mapEntityStorage);
    }

    @Override
    public Collection<User> findAll() {
        return mapEntityStorage.values();
    }

    @Override
    public Collection<User> getFriends(Integer userId) {
        if (!mapEntityStorage.containsKey(userId)) {
            LoggedException.throwNew(
                    new NotFoundException(
                            String.format("Не удалось получить список друзей пользователя id %d. "
                                    + "Пользователь не найден.", userId)), getClass());
        }
        return mapEntityStorage.get(userId).getFriends().keySet().stream()
                .map(mapEntityStorage::get).collect(Collectors.toSet());
    }

    @Override
    public User findById(Integer userId) {
        return mapEntityStorage.get(userId);
    }

    @Override
    public User create(UserCreateDto userCreateDto) {
        User user = UserMapper.toEntity(userCreateDto);
        if (!Validators.isValidLogin(user.getLogin())) {
            LoggedException.throwNew(
                    new ValidationException("Логин не должен содержать пробелы или быть пустым"), getClass());
        }
        user.setId(getNextId());
        mapEntityStorage.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User userUpdate, User userOriginal) {
        String copy = userOriginal.toString();
        if (!Validators.isValidLogin(userUpdate.getLogin())) {
            LoggedException.throwNew(
                    new ValidationException("Логин не должен содержать пробелы или быть пустым"), getClass());
        }
        for (Field field : userUpdate.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(userUpdate);
                if (value != null) {
                    field.set(userOriginal, value);
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }

        log.info("Обновлён пользователь {}. Новое значение: {}", copy, userOriginal);
        return userOriginal;
    }

    @Override
    public Integer delete(Integer userId) {
        mapEntityStorage.remove(userId);
        log.info("Удалён пользователь id {}", userId);
        return userId;
    }
}
