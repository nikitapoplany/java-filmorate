package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

/**
 * Контроллер для работы с пользователями
 */
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Получение списка всех пользователей
     *
     * @return список пользователей
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Получение пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return пользователь
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        log.info("Получен запрос на получение пользователя с id: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Создание нового пользователя
     *
     * @param user данные пользователя
     * @return созданный пользователь
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        return ResponseEntity.ok(userService.createUser(user));
    }

    /**
     * Обновление существующего пользователя
     *
     * @param user данные пользователя
     * @return обновленный пользователь
     */
    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя: {}", user);
        return ResponseEntity.ok(userService.updateUser(user));
    }

    /**
     * Добавление в друзья
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор друга
     * @return пользователь с обновленным списком друзей
     */
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос на добавление в друзья пользователя с id {} для пользователя с id {}", friendId, id);
        return ResponseEntity.ok(userService.addFriend(id, friendId));
    }

    /**
     * Удаление из друзей
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор друга
     * @return пользователь с обновленным списком друзей
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос на удаление из друзей пользователя с id {} для пользователя с id {}", friendId, id);
        return ResponseEntity.ok(userService.removeFriend(id, friendId));
    }

    /**
     * Получение списка друзей пользователя
     *
     * @param id идентификатор пользователя
     * @return список друзей
     */
    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable int id) {
        log.info("Получен запрос на получение списка друзей пользователя с id {}", id);
        return ResponseEntity.ok(userService.getFriends(id));
    }

    /**
     * Получение списка общих друзей
     *
     * @param id      идентификатор первого пользователя
     * @param otherId идентификатор второго пользователя
     * @return список общих друзей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получен запрос на получение списка общих друзей пользователей с id {} и {}", id, otherId);
        return ResponseEntity.ok(userService.getCommonFriends(id, otherId));
    }
}