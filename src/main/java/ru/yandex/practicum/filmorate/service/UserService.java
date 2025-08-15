package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(UserCreateDto userCreateDto) {
        return userStorage.create(userCreateDto);
    }

    public User update(UserUpdateDto userUpdateDto) {
        User userUpdate = UserMapper.toEntity(userUpdateDto);

        if (!userStorage.getStorage().containsKey(userUpdate.getId())) {
            LoggedException.throwNew(
                    new NotFoundException(String.format("Ошибка при обновлении пользователя" +
                            " id=%d: пользователь не найден", userUpdate.getId())), getClass());
        }

        User userOriginal = userStorage.getStorage().get(userUpdate.getId());

        return userStorage.update(userUpdate, userOriginal);
    }
}
