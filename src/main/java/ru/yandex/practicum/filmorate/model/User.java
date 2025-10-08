package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель данных пользователя
 */
@Data
public class User {
    /**
     * Идентификатор пользователя
     */
    private int id;

    /**
     * Электронная почта
     */
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть корректной")
    private String email;

    /**
     * Логин пользователя
     */
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;

    /**
     * Имя для отображения
     */
    private String name;

    /**
     * Дата рождения
     */
    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    
    /**
     * Множество идентификаторов друзей пользователя
     */
    private Set<Integer> friends = new HashSet<>();
    
    /**
     * Добавить друга
     *
     * @param friendId идентификатор друга
     * @return true, если друг был добавлен, false если пользователь уже был в друзьях
     */
    public boolean addFriend(int friendId) {
        return friends.add(friendId);
    }
    
    /**
     * Удалить друга
     *
     * @param friendId идентификатор друга
     * @return true, если друг был удален, false если пользователь не был в друзьях
     */
    public boolean removeFriend(int friendId) {
        return friends.remove(friendId);
    }
    
    /**
     * Получить множество идентификаторов друзей
     *
     * @return множество идентификаторов друзей
     */
    public Set<Integer> getFriends() {
        return friends;
    }
    
    /**
     * Проверить, является ли пользователь другом
     *
     * @param userId идентификатор пользователя
     * @return true, если пользователь является другом, иначе false
     */
    public boolean isFriend(int userId) {
        return friends.contains(userId);
    }
}