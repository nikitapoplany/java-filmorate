package ru.yandex.practicum.filmorate.storage;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class InMemoryUserStorage extends AbstractStorage<User> implements UserStorage {
    @Override
    public Map<Integer, User> getStorage() {
        return Map.copyOf(mapEntityStorage);
    }

    @Override
    public Collection<User> findAll() {
        return mapEntityStorage.values();
    }

    @Override
    public User findById(Integer userId) {
        return mapEntityStorage.get(userId);
    }

    @Override
    public User create(UserCreateDto userCreateDto) {
        User user = UserMapper.toEntity(userCreateDto);
        user.setId(getNextId());
        mapEntityStorage.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User userUpdate, User userOriginal) {
        User copy = new User(userOriginal);

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
