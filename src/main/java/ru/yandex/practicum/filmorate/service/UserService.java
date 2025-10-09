package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для работы с пользователями
 */
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
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
     */
    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    /**
     * Добавление в друзья
     *
     * @param userId    идентификатор пользователя
     * @param friendId  идентификатор друга
     * @return пользователь с обновленным списком друзей
     */
    public User addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.addFriend(friendId);
        friend.addFriend(userId);

        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
        return user;
    }

    /**
     * Удаление из друзей
     *
     * @param userId    идентификатор пользователя
     * @param friendId  идентификатор друга
     * @return пользователь с обновленным списком друзей
     */
    public User removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.removeFriend(friendId);
        friend.removeFriend(userId);

        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);
        return user;
    }

    /**
     * Получение списка друзей пользователя
     *
     * @param userId идентификатор пользователя
     * @return список друзей
     */
    public List<User> getFriends(int userId) {
        User user = userStorage.getUserById(userId);
        List<User> friends = new ArrayList<>();

        for (Integer friendId : user.getFriends()) {
            friends.add(userStorage.getUserById(friendId));
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
     */
    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);

        Set<Integer> commonFriendsIds = new HashSet<>(user.getFriends());
        commonFriendsIds.retainAll(otherUser.getFriends());

        List<User> commonFriends = commonFriendsIds.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());

        log.info("Получен список общих друзей пользователей с id {} и {}. Количество: {}",
                userId, otherId, commonFriends.size());
        return commonFriends;
    }
}