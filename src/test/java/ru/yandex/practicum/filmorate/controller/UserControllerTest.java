package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Тесты для контроллера пользователей
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldCreateUserWithValidData() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setEmail("user@example.com");
        expectedUser.setLogin("userLogin");
        expectedUser.setName("User Name");
        expectedUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenReturn(expectedUser);

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser);
        assertEquals(1, createdUser.getId());
        assertEquals("user@example.com", createdUser.getEmail());
        assertEquals("userLogin", createdUser.getLogin());
        assertEquals("User Name", createdUser.getName());
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        // Имя не задано

        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setEmail("user@example.com");
        expectedUser.setLogin("userLogin");
        expectedUser.setName("userLogin"); // Имя должно быть равно логину
        expectedUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenReturn(expectedUser);

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser);
        assertEquals("userLogin", createdUser.getName());
    }

    /**
     * Тест проверяет, что если имя пользователя состоит из пробелов, то используется логин
     */
    @Test
    void shouldUseLoginAsNameWhenNameIsBlank() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("  ");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setEmail("user@example.com");
        expectedUser.setLogin("userLogin");
        expectedUser.setName("userLogin"); // Имя должно быть равно логину
        expectedUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenReturn(expectedUser);

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser);
        assertEquals("userLogin", createdUser.getName());
    }

    /**
     * Тест проверяет, что при создании пользователя с пустой электронной почтой выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        User user = new User();
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenThrow(
                new ValidationException("Электронная почта не может быть пустой")
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(user)
        );

        assertTrue(exception.getMessage().contains("Электронная почта не может быть пустой"));
    }

    /**
     * Тест проверяет, что при создании пользователя с электронной почтой без символа @ выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenEmailDoesNotContainAtSymbol() {
        User user = new User();
        user.setEmail("userexample.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenThrow(
                new ValidationException("Электронная почта не может быть пустой и должна содержать символ @")
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(user)
        );

        assertTrue(exception.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));
    }

    /**
     * Тест проверяет, что при создании пользователя с пустым логином выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenLoginIsEmpty() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenThrow(
                new ValidationException("Логин не может быть пустым")
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(user)
        );

        assertTrue(exception.getMessage().contains("Логин не может быть пустым"));
    }

    /**
     * Тест проверяет, что при создании пользователя с логином, содержащим пробелы, выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user login");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenThrow(
                new ValidationException("Логин не может быть пустым и содержать пробелы")
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(user)
        );

        assertTrue(exception.getMessage().contains("Логин не может быть пустым и содержать пробелы"));
    }

    /**
     * Тест проверяет, что при создании пользователя с датой рождения в будущем выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.now().plusDays(1)); // Завтрашний день

        when(userService.createUser(any(User.class))).thenThrow(
                new ValidationException("Дата рождения не может быть в будущем")
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(user)
        );

        assertTrue(exception.getMessage().contains("Дата рождения не может быть в будущем"));
    }

    /**
     * Тест проверяет, что дата рождения, равная сегодняшнему дню, проходит валидацию
     */
    @Test
    void shouldAcceptBirthdayToday() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.now()); // Сегодняшний день

        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setEmail("user@example.com");
        expectedUser.setLogin("userLogin");
        expectedUser.setName("User Name");
        expectedUser.setBirthday(LocalDate.now());

        when(userService.createUser(any(User.class))).thenReturn(expectedUser);

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser);
        assertEquals(LocalDate.now(), createdUser.getBirthday());
    }

    /**
     * Тест проверяет обновление существующего пользователя
     */
    @Test
    void shouldUpdateExistingUser() {
        // Создаем пользователя для обновления
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("updated@example.com");
        updatedUser.setLogin("updatedLogin");
        updatedUser.setName("Updated Name");
        updatedUser.setBirthday(LocalDate.of(1990, 5, 5));

        // Настраиваем мок сервиса
        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        // Вызываем метод контроллера
        User result = userController.updateUser(updatedUser);

        // Проверяем результат
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("updatedLogin", result.getLogin());
        assertEquals("Updated Name", result.getName());
        assertEquals(LocalDate.of(1990, 5, 5), result.getBirthday());
    }

    /**
     * Тест проверяет, что при обновлении несуществующего пользователя выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        User user = new User();
        user.setId(999); // Несуществующий ID
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.updateUser(any(User.class))).thenThrow(
                new NotFoundException("Пользователь с id 999 не найден")
        );

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userController.updateUser(user)
        );

        assertTrue(exception.getMessage().contains("Пользователь с id 999 не найден"));
    }
    
    /**
     * Тест проверяет получение пользователя по идентификатору
     */
    @Test
    void shouldGetUserById() {
        int userId = 1;
        
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setEmail("user@example.com");
        expectedUser.setLogin("userLogin");
        expectedUser.setName("User Name");
        expectedUser.setBirthday(LocalDate.of(2000, 1, 1));
        
        when(userService.getUserById(userId)).thenReturn(expectedUser);
        
        User user = userController.getUserById(userId);
        
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("userLogin", user.getLogin());
        assertEquals("User Name", user.getName());
    }
    
    /**
     * Тест проверяет добавление друга
     */
    @Test
    void shouldAddFriend() {
        int userId = 1;
        int friendId = 2;
        
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.addFriend(friendId);
        
        when(userService.addFriend(userId, friendId)).thenReturn(user);
        
        User updatedUser = userController.addFriend(userId, friendId);
        
        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertTrue(updatedUser.getFriends().contains(friendId));
    }
    
    /**
     * Тест проверяет удаление друга
     */
    @Test
    void shouldRemoveFriend() {
        int userId = 1;
        int friendId = 2;
        
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        // Друг уже удален
        
        when(userService.removeFriend(userId, friendId)).thenReturn(user);
        
        User updatedUser = userController.removeFriend(userId, friendId);
        
        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertFalse(updatedUser.getFriends().contains(friendId));
    }
    
    /**
     * Тест проверяет получение списка друзей пользователя
     */
    @Test
    void shouldGetFriends() {
        int userId = 1;
        
        User friend1 = new User();
        friend1.setId(2);
        friend1.setEmail("friend1@example.com");
        friend1.setLogin("friend1Login");
        friend1.setName("Friend 1");
        friend1.setBirthday(LocalDate.of(2001, 1, 1));
        
        User friend2 = new User();
        friend2.setId(3);
        friend2.setEmail("friend2@example.com");
        friend2.setLogin("friend2Login");
        friend2.setName("Friend 2");
        friend2.setBirthday(LocalDate.of(2002, 2, 2));
        
        List<User> expectedFriends = List.of(friend1, friend2);
        
        when(userService.getFriends(userId)).thenReturn(expectedFriends);
        
        List<User> friends = userController.getFriends(userId);
        
        assertNotNull(friends);
        assertEquals(2, friends.size());
        assertEquals(2, friends.get(0).getId());
        assertEquals(3, friends.get(1).getId());
    }
    
    /**
     * Тест проверяет получение списка общих друзей
     */
    @Test
    void shouldGetCommonFriends() {
        int userId = 1;
        int otherId = 2;
        
        User commonFriend = new User();
        commonFriend.setId(3);
        commonFriend.setEmail("common@example.com");
        commonFriend.setLogin("commonLogin");
        commonFriend.setName("Common Friend");
        commonFriend.setBirthday(LocalDate.of(2000, 3, 3));
        
        List<User> expectedCommonFriends = List.of(commonFriend);
        
        when(userService.getCommonFriends(userId, otherId)).thenReturn(expectedCommonFriends);
        
        List<User> commonFriends = userController.getCommonFriends(userId, otherId);
        
        assertNotNull(commonFriends);
        assertEquals(1, commonFriends.size());
        assertEquals(3, commonFriends.get(0).getId());
        assertEquals("Common Friend", commonFriends.get(0).getName());
    }
}