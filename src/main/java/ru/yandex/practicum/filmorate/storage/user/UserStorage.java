package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * Интерфейс хранилища пользователей
 */
public interface UserStorage {
    /**
     * Получение списка всех пользователей
     *
     * @return список пользователей
     */
    List<User> getAllUsers();

    /**
     * Создание нового пользователя
     *
     * @param user данные пользователя
     * @return созданный пользователь
     */
    User createUser(User user);

    /**
     * Обновление существующего пользователя
     *
     * @param user данные пользователя
     * @return обновленный пользователь
     */
    User updateUser(User user);

    /**
     * Получение пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return пользователь
     */
    User getUserById(int id);

    /**
     * Проверка существования пользователя
     *
     * @param id идентификатор пользователя
     * @return true, если пользователь существует, иначе false
     */
    boolean userExists(int id);
}