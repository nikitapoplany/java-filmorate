package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Реализация хранилища пользователей в базе данных
 */
@Repository
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        log.debug("Получение списка всех пользователей");
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User createUser(User user) {
        // Если имя не указано, используем логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, используется логин: {}", user.getLogin());
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> values = Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday()
        );

        Number key = simpleJdbcInsert.executeAndReturnKey(values);
        user.setId(key.intValue());

        log.debug("Пользователь успешно создан: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        // Если имя не указано, используем логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, используется логин: {}", user.getLogin());
        }

        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (rowsAffected == 0) {
            log.warn("Пользователь с id {} не найден", user.getId());
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        // Обновляем список друзей
        updateFriendships(user);

        log.debug("Пользователь успешно обновлен: {}", user);
        return user;
    }

    @Override
    public Optional<User> getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
            if (user != null) {
                loadFriends(user);
            }
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.warn("Пользователь с id {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public boolean userExists(int id) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    /**
     * Загрузка списка друзей пользователя
     *
     * @param user пользователь
     */
    private void loadFriends(User user) {
        String sql = "SELECT friend_id FROM friendship WHERE user_id = ?";
        List<Integer> friendIds = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("friend_id"),
                user.getId());

        Set<Integer> friends = new HashSet<>(friendIds);
        user.setFriends(friends);
    }

    /**
     * Обновление списка друзей пользователя
     *
     * @param user пользователь
     */
    private void updateFriendships(User user) {
        // Удаляем все текущие записи о дружбе
        String deleteSql = "DELETE FROM friendship WHERE user_id = ?";
        jdbcTemplate.update(deleteSql, user.getId());

        // Добавляем новые записи о дружбе
        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
            String insertSql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
            for (Integer friendId : user.getFriends()) {
                jdbcTemplate.update(insertSql, user.getId(), friendId);
            }
        }
    }

    /**
     * Маппинг строки результата запроса в объект User
     *
     * @param rs     результат запроса
     * @param rowNum номер строки
     * @return объект User
     * @throws SQLException при ошибке доступа к данным
     */
    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        // Загружаем друзей
        loadFriends(user);

        return user;
    }
}