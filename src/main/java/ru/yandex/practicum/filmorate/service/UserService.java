package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

/**
 * Сервис для работы с пользователями
 */
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Получение списка всех пользователей
     *
     * @return список пользователей
     */
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    /**
     * Создание нового пользователя
     *
     * @param user данные пользователя
     * @return созданный пользователь
     */
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    /**
     * Обновление существующего пользователя
     *
     * @param user данные пользователя
     * @return обновленный пользователь
     */
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    /**
     * Получение пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return пользователь
     * @throws NotFoundException если пользователь не найден
     */
    public User getUserById(int id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    /**
     * Добавление в друзья (односторонняя дружба)
     *
     * @param userId    идентификатор пользователя
     * @param friendId  идентификатор друга
     * @return пользователь с обновленным списком друзей
     * @throws NotFoundException если пользователь или друг не найден
     */
    public User addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        // Проверяем, что друг существует
        userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        user.addFriend(friendId);

        // Обновляем пользователя в хранилище
        userStorage.updateUser(user);

        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
        return user;
    }

    /**
     * Удаление из друзей (односторонняя дружба)
     *
     * @param userId    идентификатор пользователя
     * @param friendId  идентификатор друга
     * @return пользователь с обновленным списком друзей
     * @throws NotFoundException если пользователь или друг не найден
     */
    public User removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        // Проверяем, что друг существует
        userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        user.removeFriend(friendId);

        // Обновляем пользователя в хранилище
        userStorage.updateUser(user);

        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);
        return user;
    }

    /**
     * Получение списка друзей пользователя
     *
     * @param userId идентификатор пользователя
     * @return список друзей
     * @throws NotFoundException если пользователь не найден
     */
    public List<User> getFriends(int userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        List<User> friends = new ArrayList<>();

        for (Integer friendId : user.getFriends()) {
            userStorage.getUserById(friendId)
                    .ifPresent(friends::add);
        }

        log.info("Получен список друзей пользователя с id {}. Количество: {}", userId, friends.size());
        return friends;
    }

    /**
     * Получение списка общих друзей
     *
     * @param userId    идентификатор первого пользователя
     * @param otherId   идентификатор второго пользователя
     * @return список общих друзей
     * @throws NotFoundException если пользователь не найден
     */
    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        User otherUser = userStorage.getUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + otherId + " не найден"));

        Set<Integer> commonFriendsIds = new HashSet<>(user.getFriends());
        commonFriendsIds.retainAll(otherUser.getFriends());

        List<User> commonFriends = commonFriendsIds.stream()
                .map(id -> userStorage.getUserById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден")))
                .collect(Collectors.toList());

        log.info("Получен список общих друзей пользователей с id {} и {}. Количество: {}",
                userId, otherId, commonFriends.size());
        return commonFriends;
    }
}