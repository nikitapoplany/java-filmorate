package ru.yandex.practicum.filmorate.user;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.abstraction.AbstractService;
import ru.yandex.practicum.filmorate.util.LoggedException;

import static ru.yandex.practicum.filmorate.util.Validators.validateEmail;
import static ru.yandex.practicum.filmorate.util.Validators.validateString;

@Service
public class UserService extends AbstractService<User> {

    public List<User> findAll() {
        return (List<User>) mapEntityStorage.values();
    }

    public User create(User user) {
        if (validateString(user.getName())) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        mapEntityStorage.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    public User update(User user) {
        if (user.getId() == null || !mapEntityStorage.containsKey(user.getId())) {
            LoggedException.throwNew(
                    new NotFoundException(String.format("Ошибка при обновлении пользователя" +
                            " id=%d: пользователь не найден", user.getId())), getClass());
        }

        User oldUser = mapEntityStorage.get(user.getId());

        if (user.getEmail() != null && validateEmail(user.getEmail())) {
            oldUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            if (user.getName().isBlank()) {
                oldUser.setName(oldUser.getLogin());
            } else {
                oldUser.setName(user.getName());
            }
        }

        if (user.getLogin() != null && !user.getLogin().isBlank()) {
            oldUser.setLogin(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isBefore(LocalDate.now())) {
            oldUser.setBirthday(user.getBirthday());
        }

        return oldUser;
    }
}
