package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.Collection;
import java.util.Map;

import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    //Map<Integer, User> getStorage();

    Collection<User> findAll();

    Collection<User> getFriends(Integer id);

    User findById(Integer userId);

    User create(UserCreateDto userCreateDto);

    User update(User userUpdate, User userOriginal);

    Integer delete(Integer userId);
}