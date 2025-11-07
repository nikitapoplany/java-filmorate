package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.Collection;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    //Map<Integer, User> getStorage();
    Collection<User> findAll();

    User findById(Integer userId);

    User create(User user);

    User update(User userUpdate, User userOriginal);

    Integer delete(Integer userId);

    Collection<User> getFriends(Integer id);

    Collection<User> getCommonFriends(Integer userIdA, Integer userIdB);

    void addFriend(Integer userIdA, Integer userIdB);

    void removeFriend(Integer userIdA, Integer userIdB);
}