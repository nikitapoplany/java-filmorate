package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.*;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @NotNull @RequestBody User user) {
        if (!isValidString(user.getName())) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.error("Ошибка при обновлении пользователя id={}: пользователь не найден", user.getId());
            throw new ValidationException(String.format("Ошибка при обновлении пользователя id=%d: "
                    + "пользователь не найден", user.getId()));
        }

        User oldUser = users.get(user.getId());

        if (user.getEmail() != null && isValidEmail(user.getEmail())) {
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

    private boolean isValidString(String str) {
        return str != null && !str.isBlank();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$");
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
