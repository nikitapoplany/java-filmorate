package ru.yandex.practicum.filmorate.storage.user;

import java.util.List;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    List<User> findAll();

    User findById(Integer userId);

    User create(User user);

    User update(User userUpdate);

    Integer delete(Integer userId);

    List<User> getFriends(Integer id);

    List<User> getCommonFriends(Integer userIdA, Integer userIdB);

    void addFriend(Integer userIdA, Integer userIdB);

    void removeFriend(Integer userIdA, Integer userIdB);
}