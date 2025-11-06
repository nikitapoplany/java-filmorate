package ru.yandex.practicum.filmorate.storage;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

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
public class UserDbStorage implements UserStorage {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper mapper;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public Collection<User> findAll() {
        String query = "SELECT * FROM \"user\";";
        return jdbcTemplate.query(query, mapper);
    }

    @Override
    public Collection<User> getFriends(Integer userId) {
        findById(userId);

        String query = """
                SELECT u.id, u.email, u.login, u.name, u.birthday
                FROM friends f
                JOIN "user" u ON f.request_to_id = u.id
                WHERE f.request_from_id = ?
                """;
        Collection<User> response = jdbcTemplate.query(query, mapper, userId);
        if (response.isEmpty()) {
            response = new HashSet<>();
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
            LoggedException.throwNew(
                    new NotFoundException(String.format("Не удалось удалить пользователя id %d "
                                    + "из друзей пользователя id %d. Один из пользователей "
                                    + "не найден, или они не являются друзьями.",
                            userIdB, userIdA)),
                    getClass());
        }
    }

    @Override
    public Collection<User> getCommonFriends(Integer userIdA, Integer userIdB) {
        Set<Integer> userAFriends = getFriends(userIdA)
                .stream()
                .mapToInt(User::getId)
                .boxed()
                .collect(Collectors.toSet());

        Set<Integer> userBFriends = getFriends(userIdB)
                .stream()
                .mapToInt(User::getId)
                .boxed()
                .collect(Collectors.toSet());

        List<User> result = new ArrayList<>();

        for (Integer id : userAFriends) {
            if (userBFriends.contains(id)) {
                result.add(findById(id));
            }
        }

        return result;
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
    public User update(User userUpdate, User userOriginal) {
        String copy = userOriginal.toString();

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