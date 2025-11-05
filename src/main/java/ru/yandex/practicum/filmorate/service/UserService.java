package ru.yandex.practicum.filmorate.service;

import java.util.*;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.util.Validators;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public Collection<User> getFriends(Integer userId) {
        return userStorage.getFriends(userId);
    }

    public User findById(Integer userId) {
        return userStorage.findById(userId);
    }

    public User create(UserCreateDto userCreateDto) {
        User user = UserMapper.toEntity(userCreateDto);

        if (!Validators.isValidLogin(user.getLogin())) {
            LoggedException.throwNew(
                    new ValidationException("Логин не должен содержать пробелы или быть пустым"), getClass());
        }
        return userStorage.create(user);
    }

    public User update(UserUpdateDto userUpdateDto) {
        User userUpdate = UserMapper.toEntity(userUpdateDto);

        if (Optional.ofNullable(userStorage.findById(userUpdate.getId())).isEmpty()) {
            LoggedException.throwNew(
                    new NotFoundException(String.format("Ошибка при обновлении пользователя" +
                                                        " id %d: пользователь не найден", userUpdate.getId())), getClass());
        }

        User userOriginal = userStorage.findById(userUpdate.getId());

        if (!Validators.isValidLogin(userUpdate.getLogin())) {
            LoggedException.throwNew(
                    new ValidationException("Логин не должен содержать пробелы или быть пустым"), getClass());
        }

        return userStorage.update(userUpdate, userOriginal);
    }

    public void addFriend(Integer userIdA, Integer userIdB) {
        userStorage.addFriend(userIdA, userIdB);
    }

    public void removeFriend(Integer userIdA, Integer userIdB) {
        userStorage.removeFriend(userIdA, userIdB);
    }

    public Set<User> getCommonFriends(Integer userIdA, Integer userIdB) {
        return new HashSet<>(userStorage.getCommonFriends(userIdA, userIdB));
    }
}
