package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

/**
 * Конфигурация для тестов
 * Эта конфигурация используется в интеграционных тестах для явной регистрации
 * бинов, которые не создаются автоматически аннотацией @JdbcTest
 */
@Configuration
public class TestConfig {

    /**
     * Создает бин UserDbStorage для тестов
     *
     * @param jdbcTemplate шаблон JDBC
     * @return экземпляр UserDbStorage
     */
    @Bean
    public UserDbStorage userDbStorage(JdbcTemplate jdbcTemplate) {
        return new UserDbStorage(jdbcTemplate);
    }
}