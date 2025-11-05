package ru.yandex.practicum.filmorate.storage;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendStatus;
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
    public void addFriend(Integer userIdA, Integer userIdB) {
        Optional<User> userA = Optional.ofNullable(findById(userIdA));
        Optional<User> userB = Optional.ofNullable(findById(userIdB));

        if (userA.isPresent() && userB.isPresent()) {
            userA.get().getFriends().put(userIdB, FriendStatus.PENDING);
        } else {
            int missingId;

            if (userA.isEmpty()) {
                missingId = userIdA;
            } else {
                missingId = userIdB;
            }
            LoggedException.throwNew(
                    new NotFoundException(String.format("Не удалось добавить друга у пользователя id %d."
                            + " Пользователь с таким id не найден.", missingId)),
                    getClass());
        }
    }

    @Override
    public void removeFriend(Integer userIdA, Integer userIdB) {
        Optional<User> userA = Optional.ofNullable(findById(userIdA));
        if (userA.isPresent() && userA.get().getFriends().containsKey(userIdB)) {
            userA.get().getFriends().remove(userIdB);
        } else {
            LoggedException.throwNew(
                    new NotFoundException(String.format("Не удалось удалить пользователя id %d "
                                    + "из друзей пользователя id %d. Один из пользователей "
                                    + "не найден, или они не являются друзьями.",
                            userIdB, userIdA)),
                    getClass());
        }
    }

    @Override
    public Collection<User> getCommonFriends(Integer userIdA, Integer userIdB) {
        Set<Integer> userAFriends = findById(userIdA).getFriends().keySet();
        Set<Integer> userBFriends = findById(userIdB).getFriends().keySet();

        List<User> result = new ArrayList<>();

        for (Integer id : userAFriends) {
            if (userBFriends.contains(id)) {
                result.add(findById(id));
            }
        }

        return result;
    }

    @Override
    public User create(User user) {
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
