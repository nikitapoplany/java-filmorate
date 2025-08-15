package ru.yandex.practicum.filmorate.service;

import java.util.*;
import java.util.stream.Collectors;

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

    public void addFriend(Integer userIdA, Integer userIdB) {
        Optional<User> userA = Optional.ofNullable(userStorage.findById(userIdA));
        Optional<User> userB = Optional.ofNullable(userStorage.findById(userIdB));

        if (userA.isPresent() && userB.isPresent()) {
            userA.get().getFriends().add(userIdB);
            userB.get().getFriends().add(userIdB);
        } else {
            int missingId;

            if (userA.isEmpty()) {
                missingId = userIdA;
            } else {
                missingId = userIdB;
            }
            throw new NotFoundException(String.format("Не удалось добавить друга у пользователя id %d."
                    + " Пользователь с таким id не найден.", missingId));
        }
    }

    public void removeFriend(Integer userIdA, Integer userIdB) {
        Optional<User> userA = Optional.ofNullable(userStorage.findById(userIdA));
        Optional<User> userB = Optional.ofNullable(userStorage.findById(userIdB));

        if (userA.isPresent() && userB.isPresent()) {
            userA.get().getFriends().remove(userIdB);
            userB.get().getFriends().remove(userIdB);
        } else {
            int missingId;

            if (userA.isEmpty()) {
                missingId = userIdA;
            } else {
                missingId = userIdB;
            }
            throw new NotFoundException(String.format("Не удалось удалить друга у пользователя id %d."
                    + " Пользователь с таким id не найден.", missingId));
        }
    }

    public Set<User> findMutualFriends(Integer userIdA, Integer userIdB) {
        Optional<User> userA = Optional.ofNullable(userStorage.findById(userIdA));
        Optional<User> userB = Optional.ofNullable(userStorage.findById(userIdB));

        if (userA.isPresent() && userB.isPresent()) {
            Set<Integer> userAFriends = userA.get().getFriends();
            Set<Integer> userBFriends = userB.get().getFriends();

            Set<Integer> minSet = userAFriends.size() < userBFriends.size() ? userAFriends : userBFriends;
            Set<Integer> maxSet = minSet.equals(userAFriends) ? userBFriends : userAFriends;

            return minSet.stream()
                    .filter(maxSet::contains)
                    .map(userStorage::findById)
                    .collect(Collectors.toSet());
        }  else {
            int missingId;

            if (userA.isEmpty()) {
                missingId = userIdA;
            } else {
                missingId = userIdB;
            }
            throw new NotFoundException(String.format("Не удалось найти общих друзей у пользователей id %d и id %d."
                    + " Пользователь с id %d не найден.", userIdA, userIdB, missingId));
        }
    }
}
