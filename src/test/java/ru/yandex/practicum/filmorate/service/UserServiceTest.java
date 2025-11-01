package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервиса пользователей
 */
class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест проверяет получение всех пользователей
     */
    @Test
    void shouldGetAllUsers() {
        // Подготовка данных
        User user1 = new User();
        user1.setId(1);
        user1.setEmail("user1@example.com");
        user1.setLogin("user1Login");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("user2@example.com");
        user2.setLogin("user2Login");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(2001, 1, 1));

        List<User> expectedUsers = List.of(user1, user2);

        // Настройка мока
        when(userStorage.getAllUsers()).thenReturn(expectedUsers);

        // Вызов тестируемого метода
        List<User> users = userService.getAllUsers();

        // Проверка результатов
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(1, users.get(0).getId());
        assertEquals(2, users.get(1).getId());

        // Проверка вызова метода хранилища
        verify(userStorage, times(1)).getAllUsers();
    }

    /**
     * Тест проверяет создание пользователя
     */
    @Test
    void shouldCreateUser() {
        // Подготовка данных
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

        // Настройка мока
        when(userStorage.createUser(any(User.class))).thenReturn(expectedUser);

        // Вызов тестируемого метода
        User createdUser = userService.createUser(user);

        // Проверка результатов
        assertNotNull(createdUser);
        assertEquals(1, createdUser.getId());
        assertEquals("user@example.com", createdUser.getEmail());
        assertEquals("userLogin", createdUser.getLogin());
        assertEquals("User Name", createdUser.getName());

        // Проверка вызова метода хранилища
        verify(userStorage, times(1)).createUser(user);
    }

    /**
     * Тест проверяет обновление пользователя
     */
    @Test
    void shouldUpdateUser() {
        // Подготовка данных
        User user = new User();
        user.setId(1);
        user.setEmail("updated@example.com");
        user.setLogin("updatedLogin");
        user.setName("Updated Name");
        user.setBirthday(LocalDate.of(1990, 5, 5));

        // Настройка мока
        when(userStorage.updateUser(any(User.class))).thenReturn(user);

        // Вызов тестируемого метода
        User updatedUser = userService.updateUser(user);

        // Проверка результатов
        assertNotNull(updatedUser);
        assertEquals(1, updatedUser.getId());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("updatedLogin", updatedUser.getLogin());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals(LocalDate.of(1990, 5, 5), updatedUser.getBirthday());

        // Проверка вызова метода хранилища
        verify(userStorage, times(1)).updateUser(user);
    }

    /**
     * Тест проверяет получение пользователя по идентификатору
     */
    @Test
    void shouldGetUserById() {
        // Подготовка данных
        int userId = 1;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setEmail("user@example.com");
        expectedUser.setLogin("userLogin");
        expectedUser.setName("User Name");
        expectedUser.setBirthday(LocalDate.of(2000, 1, 1));

        // Настройка мока
        when(userStorage.getUserById(userId)).thenReturn(expectedUser);

        // Вызов тестируемого метода
        User user = userService.getUserById(userId);

        // Проверка результатов
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("userLogin", user.getLogin());
        assertEquals("User Name", user.getName());

        // Проверка вызова метода хранилища
        verify(userStorage, times(1)).getUserById(userId);
    }

    /**
     * Тест проверяет добавление друга
     */
    @Test
    void shouldAddFriend() {
        // Подготовка данных
        int userId = 1;
        int friendId = 2;

        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User friend = new User();
        friend.setId(friendId);
        friend.setEmail("friend@example.com");
        friend.setLogin("friendLogin");
        friend.setName("Friend Name");
        friend.setBirthday(LocalDate.of(2001, 1, 1));

        // Настройка моков
        when(userStorage.getUserById(userId)).thenReturn(user);
        when(userStorage.getUserById(friendId)).thenReturn(friend);

        // Вызов тестируемого метода
        User updatedUser = userService.addFriend(userId, friendId);

        // Проверка результатов
        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertTrue(updatedUser.getFriends().contains(friendId));
        assertTrue(friend.getFriends().contains(userId));

        // Проверка вызова методов хранилища
        verify(userStorage, times(1)).getUserById(userId);
        verify(userStorage, times(1)).getUserById(friendId);
    }

    /**
     * Тест проверяет, что при добавлении в друзья несуществующего пользователя выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenAddingNonExistentFriend() {
        // Подготовка данных
        int userId = 1;
        int friendId = 999;

        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // Настройка моков
        when(userStorage.getUserById(userId)).thenReturn(user);
        when(userStorage.getUserById(friendId)).thenThrow(new NotFoundException("Пользователь с id " + friendId + " не найден"));

        // Проверка исключения
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.addFriend(userId, friendId)
        );

        assertTrue(exception.getMessage().contains("Пользователь с id 999 не найден"));

        // Проверка вызова методов хранилища
        verify(userStorage, times(1)).getUserById(userId);
        verify(userStorage, times(1)).getUserById(friendId);
    }

    /**
     * Тест проверяет удаление друга
     */
    @Test
    void shouldRemoveFriend() {
        // Подготовка данных
        int userId = 1;
        int friendId = 2;

        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.addFriend(friendId);

        User friend = new User();
        friend.setId(friendId);
        friend.setEmail("friend@example.com");
        friend.setLogin("friendLogin");
        friend.setName("Friend Name");
        friend.setBirthday(LocalDate.of(2001, 1, 1));
        friend.addFriend(userId);

        // Настройка моков
        when(userStorage.getUserById(userId)).thenReturn(user);
        when(userStorage.getUserById(friendId)).thenReturn(friend);

        // Вызов тестируемого метода
        User updatedUser = userService.removeFriend(userId, friendId);

        // Проверка результатов
        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertFalse(updatedUser.getFriends().contains(friendId));
        assertFalse(friend.getFriends().contains(userId));

        // Проверка вызова методов хранилища
        verify(userStorage, times(1)).getUserById(userId);
        verify(userStorage, times(1)).getUserById(friendId);
    }

    /**
     * Тест проверяет получение списка друзей пользователя
     */
    @Test
    void shouldGetFriends() {
        // Подготовка данных
        int userId = 1;
        int friendId1 = 2;
        int friendId2 = 3;

        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.addFriend(friendId1);
        user.addFriend(friendId2);

        User friend1 = new User();
        friend1.setId(friendId1);
        friend1.setEmail("friend1@example.com");
        friend1.setLogin("friend1Login");
        friend1.setName("Friend 1");
        friend1.setBirthday(LocalDate.of(2001, 1, 1));

        User friend2 = new User();
        friend2.setId(friendId2);
        friend2.setEmail("friend2@example.com");
        friend2.setLogin("friend2Login");
        friend2.setName("Friend 2");
        friend2.setBirthday(LocalDate.of(2002, 2, 2));

        // Настройка моков
        when(userStorage.getUserById(userId)).thenReturn(user);
        when(userStorage.getUserById(friendId1)).thenReturn(friend1);
        when(userStorage.getUserById(friendId2)).thenReturn(friend2);

        // Вызов тестируемого метода
        List<User> friends = userService.getFriends(userId);

        // Проверка результатов
        assertNotNull(friends);
        assertEquals(2, friends.size());
        assertEquals(friendId1, friends.get(0).getId());
        assertEquals(friendId2, friends.get(1).getId());

        // Проверка вызова методов хранилища
        verify(userStorage, times(1)).getUserById(userId);
        verify(userStorage, times(1)).getUserById(friendId1);
        verify(userStorage, times(1)).getUserById(friendId2);
    }

    /**
     * Тест проверяет получение списка общих друзей
     */
    @Test
    void shouldGetCommonFriends() {
        // Подготовка данных
        int userId = 1;
        int otherId = 2;
        int commonFriendId = 3;

        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.addFriend(commonFriendId);

        User otherUser = new User();
        otherUser.setId(otherId);
        otherUser.setEmail("other@example.com");
        otherUser.setLogin("otherLogin");
        otherUser.setName("Other User");
        otherUser.setBirthday(LocalDate.of(2001, 1, 1));
        otherUser.addFriend(commonFriendId);

        User commonFriend = new User();
        commonFriend.setId(commonFriendId);
        commonFriend.setEmail("common@example.com");
        commonFriend.setLogin("commonLogin");
        commonFriend.setName("Common Friend");
        commonFriend.setBirthday(LocalDate.of(2002, 2, 2));

        // Настройка моков
        when(userStorage.getUserById(userId)).thenReturn(user);
        when(userStorage.getUserById(otherId)).thenReturn(otherUser);
        when(userStorage.getUserById(commonFriendId)).thenReturn(commonFriend);

        // Вызов тестируемого метода
        List<User> commonFriends = userService.getCommonFriends(userId, otherId);

        // Проверка результатов
        assertNotNull(commonFriends);
        assertEquals(1, commonFriends.size());
        assertEquals(commonFriendId, commonFriends.get(0).getId());
        assertEquals("Common Friend", commonFriends.get(0).getName());

        // Проверка вызова методов хранилища
        verify(userStorage, times(1)).getUserById(userId);
        verify(userStorage, times(1)).getUserById(otherId);
        verify(userStorage, times(1)).getUserById(commonFriendId);
    }
}