package ru.yandex.practicum.filmorate.storage;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorage implements UserStorage {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper mapper;

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM \"user\";";
        return jdbcTemplate.query(query, mapper);
    }

    @Override
    public User findById(Integer userId) {
        String query = "SELECT * FROM \"user\" WHERE id = ?;";
        List<User> result = jdbcTemplate.query(query, mapper, userId);
        if (result.isEmpty()) {
            LoggedException.throwNew(new NotFoundException(String.format("Не удалось получить пользователя id %d. "
                                          + "Пользователь не найден.", userId)), getClass());
        }
        return result.getFirst();
    }

    @Override
    public User create(User user) {
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
    public User update(User user) {
        String query = """
                    UPDATE "user"
                    SET email = ?, login = ?, name = ?, birthday = ?
                    WHERE "user".id = ?;
                """;

        int updatedRows = jdbcTemplate.update(
                query,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        if (updatedRows != 0) {
            log.info("Обновлён пользователь id {}. Новое значение: {}", user.getId(), user);
        } else {
            LoggedException.throwNew(new NotFoundException(String.format("Не удалось обновить пользователя id %d. "
                                          + "Пользователь не найден.", user.getId())), getClass());
        }
        return user;
    }

    @Override
    public Integer delete(Integer userId) {
        String query = "DELETE FROM \"user\" WHERE id = ?";
        int deletedRows = jdbcTemplate.update(query, userId);
        if (deletedRows != 0) {
            log.info("Удалён пользователь id {}", userId);
        } else {
            LoggedException.throwNew(new NotFoundException(String.format("Не удалось удалить пользователя id %d. "
                                          + "Пользователь не найден.", userId)), getClass());
        }
        return userId;
    }

    @Override
    public List<User> getFriends(Integer userId) {
        findById(userId);

        String query = """
                SELECT u.id, u.email, u.login, u.name, u.birthday
                FROM friends f
                JOIN "user" u ON f.request_to_id = u.id
                WHERE f.request_from_id = ?
                """;
        List<User> response = jdbcTemplate.query(query, mapper, userId);
        if (response.isEmpty()) {
            response = new ArrayList<>();
        }
        return response;
    }

    @Override
    public List<User> getCommonFriends(Integer userIdA, Integer userIdB) {
        long start = System.nanoTime();
        String query = """
                SELECT u.* FROM "user" u
                JOIN friends a
                  ON a.request_to_id = u.id
                JOIN friends b
                  ON a.request_to_id = b.request_to_id
                WHERE a.request_from_id = ?
                  AND b.request_from_id = ?;
                """;
        List<User> commonFriends = jdbcTemplate.query(query, mapper, userIdA, userIdB);
        long end = System.nanoTime();
        System.out.println("Время выполнения: " + (end - start) / 1_000_000.0 + " мс");
        return commonFriends;
    }

    @Override
    public void addFriend(Integer userIdA, Integer userIdB) {
        String query = """
                INSERT INTO FRIENDS (REQUEST_FROM_ID, REQUEST_TO_ID)
                values(?, ?);
                """;
        try {
            jdbcTemplate.update(query, userIdA, userIdB);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Referential integrity constraint violation")) {
                LoggedException.throwNew(
                        new NotFoundException(
                                String.format("Не удалось добавить друга с id %d пользователю id %d."
                                              + "Убедитесь, что id пользователей указаны верно.", userIdB, userIdA)),
                        getClass());
            }
        }
    }

    @Override
    public void removeFriend(Integer userIdA, Integer userIdB) {
        String query = """
                DELETE FROM friends
                WHERE request_from_id = ?
                AND request_to_id = ?;
                """;
        int result = jdbcTemplate.update(query, userIdA, userIdB);
        if (result == 0) {
            LoggedException.throwNew(new NotFoundException(String.format("Не удалось удалить пользователя id %d "
                                                        + "из друзей пользователя id %d. Один из пользователей "
                                                        + "не найден, или они не являются друзьями.", userIdB, userIdA))
                    ,getClass()
            );
        }
    }
}