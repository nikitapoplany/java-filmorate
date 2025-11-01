package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для хранилища пользователей в памяти
 */
class InMemoryUserStorageTest {

    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
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

        // Вызов тестируемого метода
        User createdUser = userStorage.createUser(user);

        // Проверка результатов
        assertNotNull(createdUser);
        assertEquals(1, createdUser.getId());
        assertEquals("user@example.com", createdUser.getEmail());
        assertEquals("userLogin", createdUser.getLogin());
        assertEquals("User Name", createdUser.getName());
        assertEquals(LocalDate.of(2000, 1, 1), createdUser.getBirthday());
    }

    /**
     * Тест проверяет, что если имя пользователя не указано, то используется логин
     */
    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() {
        // Подготовка данных
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        // Имя не задано

        // Вызов тестируемого метода
        User createdUser = userStorage.createUser(user);

        // Проверка результатов
        assertNotNull(createdUser);
        assertEquals("userLogin", createdUser.getName());
    }

    /**
     * Тест проверяет, что если имя пользователя состоит из пробелов, то используется логин
     */
    @Test
    void shouldUseLoginAsNameWhenNameIsBlank() {
        // Подготовка данных
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("  ");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // Вызов тестируемого метода
        User createdUser = userStorage.createUser(user);

        // Проверка результатов
        assertNotNull(createdUser);
        assertEquals("userLogin", createdUser.getName());
    }

    /**
     * Тест проверяет, что при создании пользователя с пустой электронной почтой выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenCreatingUserWithEmptyEmail() {
        // Подготовка данных
        User user = new User();
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // Проверка исключения
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.createUser(user)
        );

        assertTrue(exception.getMessage().contains("Электронная почта не может быть пустой"));
    }

    /**
     * Тест проверяет, что при создании пользователя с электронной почтой без символа @ выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenCreatingUserWithInvalidEmail() {
        // Подготовка данных
        User user = new User();
        user.setEmail("userexample.com"); // Отсутствует символ @
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // Проверка исключения
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.createUser(user)
        );

        assertTrue(exception.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));
    }

    /**
     * Тест проверяет, что при создании пользователя с пустым логином выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenCreatingUserWithEmptyLogin() {
        // Подготовка данных
        User user = new User();
        user.setEmail("user@example.com");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // Проверка исключения
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.createUser(user)
        );

        assertTrue(exception.getMessage().contains("Логин не может быть пустым"));
    }

    /**
     * Тест проверяет, что при создании пользователя с логином, содержащим пробелы, выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenCreatingUserWithLoginContainingSpaces() {
        // Подготовка данных
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user login"); // Логин содержит пробел
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // Проверка исключения
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.createUser(user)
        );

        assertTrue(exception.getMessage().contains("Логин не может быть пустым и содержать пробелы"));
    }

    /**
     * Тест проверяет, что при создании пользователя с датой рождения в будущем выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenCreatingUserWithFutureBirthday() {
        // Подготовка данных
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.now().plusDays(1)); // Завтрашний день

        // Проверка исключения
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.createUser(user)
        );

        assertTrue(exception.getMessage().contains("Дата рождения не может быть в будущем"));
    }

    /**
     * Тест проверяет обновление пользователя
     */
    @Test
    void shouldUpdateUser() {
        // Подготовка данных - сначала создаем пользователя
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userStorage.createUser(user);

        // Подготовка данных для обновления
        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updated@example.com");
        updatedUser.setLogin("updatedLogin");
        updatedUser.setName("Updated Name");
        updatedUser.setBirthday(LocalDate.of(1990, 5, 5));

        // Вызов тестируемого метода
        User result = userStorage.updateUser(updatedUser);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(createdUser.getId(), result.getId());
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
        // Подготовка данных
        User user = new User();
        user.setId(999); // Несуществующий ID
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // Проверка исключения
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userStorage.updateUser(user)
        );

        assertTrue(exception.getMessage().contains("Пользователь с id 999 не найден"));
    }

    /**
     * Тест проверяет получение всех пользователей
     */
    @Test
    void shouldGetAllUsers() {
        // Подготовка данных - создаем двух пользователей
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1Login");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2Login");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(2001, 1, 1));
        userStorage.createUser(user2);

        // Вызов тестируемого метода
        List<User> users = userStorage.getAllUsers();

        // Проверка результатов
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("User 1", users.get(0).getName());
        assertEquals("User 2", users.get(1).getName());
    }

    /**
     * Тест проверяет получение пользователя по идентификатору
     */
    @Test
    void shouldGetUserById() {
        // Подготовка данных - создаем пользователя
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userStorage.createUser(user);

        // Вызов тестируемого метода
        User retrievedUser = userStorage.getUserById(createdUser.getId());

        // Проверка результатов
        assertNotNull(retrievedUser);
        assertEquals(createdUser.getId(), retrievedUser.getId());
        assertEquals("user@example.com", retrievedUser.getEmail());
        assertEquals("userLogin", retrievedUser.getLogin());
        assertEquals("User Name", retrievedUser.getName());
    }

    /**
     * Тест проверяет, что при получении несуществующего пользователя выбрасывается исключение
     */
    @Test
    void shouldThrowExceptionWhenGettingNonExistentUser() {
        // Проверка исключения
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userStorage.getUserById(999)
        );

        assertTrue(exception.getMessage().contains("Пользователь с id 999 не найден"));
    }

    /**
     * Тест проверяет проверку существования пользователя
     */
    @Test
    void shouldCheckUserExists() {
        // Подготовка данных - создаем пользователя
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userStorage.createUser(user);

        // Проверка результатов
        assertTrue(userStorage.userExists(createdUser.getId()));
        assertFalse(userStorage.userExists(999));
    }
}