package ru.yandex.practicum.filmorate.service;

import java.util.*;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.model.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.util.*;

@Service
public class UserService {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final UserStorage userStorage;
    private final ValidatorsDb validatorsDb;
    private final UserMapper mapper;
    private final DtoHelper dtoHelper;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       ValidatorsDb validatorsDb,
                       UserMapper mapper,
                       DtoHelper dtoHelper) {
        this.userStorage = userStorage;
        this.validatorsDb = validatorsDb;
        this.mapper = mapper;
        this.dtoHelper = dtoHelper;
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
        User user = mapper.toEntity(userCreateDto);

        if (!Validators.isValidLogin(user.getLogin())) {
            LoggedException.throwNew(
                    new ValidationException("Логин не должен содержать пробелы или быть пустым"), getClass());
        }
        return userStorage.create(user);
    }

    public User update(UserUpdateDto userUpdateDto) {
        if (Optional.ofNullable(userStorage.findById(userUpdateDto.getId())).isEmpty()) {
            LoggedException.throwNew(
                    new NotFoundException(String.format("Ошибка при обновлении пользователя" +
                            " id %d: пользователь не найден", userUpdateDto.getId())), getClass());
        }

        User userUpdate = mapper.toEntity(userUpdateDto);
        User userOriginal = userStorage.findById(userUpdate.getId());

        if (!Validators.isValidLogin(userUpdate.getLogin())) {
            LoggedException.throwNew(
                    new ValidationException("Логин не должен содержать пробелы или быть пустым"), getClass());
        }

        userUpdate = (User) dtoHelper.transferFields(userOriginal, userUpdate);

        return userStorage.update(userUpdate);
    }

    public void addFriend(Integer userIdA, Integer userIdB) {
        userStorage.addFriend(userIdA, userIdB);
    }

    public void removeFriend(Integer userIdA, Integer userIdB) {
        findById(userIdA);
        findById(userIdB);
        if (!validatorsDb.isValidFriend(userIdA, userIdB)) {
            return;
        }
        userStorage.removeFriend(userIdA, userIdB);
    }

    public Set<User> getCommonFriends(Integer userIdA, Integer userIdB) {
        return new HashSet<>(userStorage.getCommonFriends(userIdA, userIdB));
    }
}
