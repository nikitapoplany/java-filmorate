package ru.yandex.practicum.filmorate.storage;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.util.Validators;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper mapper;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Collection<User> findAll() {
        String query = "SELECT * FROM \"user\";";
        return jdbcTemplate.query(query, mapper);
    }

    @Override
    public Collection<User> getFriends(Integer userId) {
        String query = """
                SELECT u.id, u.email, u.login, u.name, u.birthday
                FROM friends f
                JOIN "user" u ON f.request_to_id = u.id
                WHERE f.request_from_id = ?
                """;
        Collection<User> response = jdbcTemplate.query(query, mapper, userId);
        if (response.isEmpty()) {
            LoggedException.throwNew(
                    new NotFoundException(
                            String.format("Не удалось получить список друзей пользователя id %d. "
                                          + "Пользователь не найден.", userId)), getClass());
        }
        return response;
    }

    @Override
    public User findById(Integer userId) {
        String query = "SELECT * FROM \"user\" u WHERE u.id = ?;";
        List<User> result = jdbcTemplate.query(query, mapper, userId);
        if (result.isEmpty()) {
            LoggedException.throwNew(
                    new NotFoundException(
                            String.format("Не удалось получить пользователя id %d. "
                                          + "Пользователь не найден.", userId)), getClass());
        }
        return result.getFirst();
    }

    @Override
    public User create(UserCreateDto dto) {
        User user = UserMapper.toEntity(dto);

        if (!Validators.isValidLogin(user.getLogin())) {
            LoggedException.throwNew(
                    new ValidationException("Логин не должен содержать пробелы или быть пустым"), getClass());
        }
        String query = """
                INSERT INTO "user" (EMAIL, LOGIN, NAME, BIRTHDAY)
                VALUES (?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
                    ps.setString(1, user.getEmail());
                    ps.setString(2, user.getLogin());
                    ps.setString(3, user.getName());
                    ps.setDate(4, Date.valueOf(user.getBirthday()));
                    return ps;
                }, keyHolder);

        if (Optional.ofNullable(keyHolder.getKey()).isPresent()) {
            user.setId(keyHolder.getKey().intValue());
            log.info("Добавлен новый пользователь: {}", user);
            return user;
        }

        throw new RuntimeException("Непредвиденная ошибка при добавлении пользователя.");
    }

    @Override
    public User update(User userUpdate, User userOriginal) {
        String copy = userOriginal.toString();
        if (!Validators.isValidLogin(userUpdate.getLogin())) {
            LoggedException.throwNew(
                    new ValidationException("Логин не должен содержать пробелы или быть пустым"), getClass());
        }
        for (Field field : userUpdate.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(userUpdate);
                if (value != null) {
                    field.set(userOriginal, value);
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }

        String query = """
                UPDATE "user"
                SET email = ?, login = ?, name = ?, birthday = ?
                WHERE "user".id = ?;
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int updatedRows = jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
                    ps.setString(1, userOriginal.getEmail());
                    ps.setString(2, userOriginal.getLogin());
                    ps.setString(3, userOriginal.getName());
                    ps.setDate(4, Date.valueOf(userOriginal.getBirthday()));
                    ps.setInt(5, userOriginal.getId());
                    return ps;
                }, keyHolder);
        if (updatedRows != 0) {
            log.info("Обновлён пользователь {}. Новое значение: {}", copy, userOriginal);
        } else {
            LoggedException.throwNew(
                    new NotFoundException(
                            String.format("Не удалось обновить пользователя id %d. "
                                          + "Пользователь не найден.", userOriginal.getId())), getClass());
        }

        return userOriginal;
    }

    @Override
    public Integer delete(Integer userId) {
        String query = "DELETE FROM \"user\" WHERE id = ?";
        int deletedRows = jdbcTemplate.update(query);
        if (deletedRows != 0) {
            log.info("Удалён пользователь id {}", userId);
        } else {
            LoggedException.throwNew(
                    new NotFoundException(
                            String.format("Не удалось удалить пользователя id %d. "
                                          + "Пользователь не найден.", userId)), getClass());
        }
        return userId;
    }
}