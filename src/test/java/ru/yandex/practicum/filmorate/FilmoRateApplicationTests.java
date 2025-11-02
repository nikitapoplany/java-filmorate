package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.config.TestConfig;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Интеграционные тесты для хранилища пользователей
 */
@JdbcTest
@AutoConfigureTestDatabase
@Import(TestConfig.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
	private final UserDbStorage userStorage;
	private final JdbcTemplate jdbcTemplate;

	@BeforeEach
	public void setUp() {
		// Очищаем таблицу пользователей перед каждым тестом
		jdbcTemplate.update("DELETE FROM friendship");
		jdbcTemplate.update("DELETE FROM users");
		// Добавляем тестового пользователя с id = 1
		jdbcTemplate.update(
			"INSERT INTO users (user_id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)",
			1, "test@example.com", "testuser", "Test User", LocalDate.of(2000, 1, 1)
		);
	}

	@Test
	public void testFindUserById() {
		Optional<User> userOptional = userStorage.getUserById(1);

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
	}
}