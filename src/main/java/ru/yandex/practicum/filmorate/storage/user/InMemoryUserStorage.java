package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация хранилища пользователей в памяти
 */
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public List<User> getAllUsers() {
        log.debug("Получение списка всех пользователей. Количество: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        log.debug("Создание пользователя: {}", user);
        validateUser(user);
        user.setId(nextId++);

        // Если имя не указано, используем логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, используется логин: {}", user.getLogin());
        }

        users.put(user.getId(), user);
        log.debug("Пользователь успешно создан: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.debug("Обновление пользователя: {}", user);
        validateUser(user);

        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с id {} не найден", user.getId());
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        // Если имя не указано, используем логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, используется логин: {}", user.getLogin());
        }

        users.put(user.getId(), user);
        log.debug("Пользователь успешно обновлен: {}", user);
        return user;
    }

    @Override
    public Optional<User> getUserById(int id) {
        log.debug("Получение пользователя по id: {}", id);
        if (!users.containsKey(id)) {
            log.warn("Пользователь с id {} не найден", id);
            return Optional.empty();
        }
        return Optional.of(users.get(id));
    }

    @Override
    public boolean userExists(int id) {
        return users.containsKey(id);
    }

    /**
     * Валидация данных пользователя
     *
     * @param user данные пользователя
     * @throws ValidationException если данные не прошли валидацию
     */
    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}