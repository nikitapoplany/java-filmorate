package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель данных фильма
 */
@Data
public class Film {
    /**
     * Идентификатор фильма
     */
    private int id;

    /**
     * Название фильма
     */
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    /**
     * Описание фильма
     */
    @Size(max = 200, message = "Описание фильма не может быть длиннее 200 символов")
    private String description;

    /**
     * Дата релиза
     */
    @NotNull(message = "Дата релиза не может быть пустой")
    private LocalDate releaseDate;

    /**
     * Продолжительность фильма в минутах
     */
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    
    /**
     * Множество идентификаторов пользователей, поставивших лайк фильму
     */
    private Set<Integer> likes = new HashSet<>();
    
    /**
     * Добавить лайк от пользователя
     *
     * @param userId идентификатор пользователя
     * @return true, если лайк был добавлен, false если пользователь уже поставил лайк
     */
    public boolean addLike(int userId) {
        return likes.add(userId);
    }
    
    /**
     * Удалить лайк от пользователя
     *
     * @param userId идентификатор пользователя
     * @return true, если лайк был удален, false если пользователь не ставил лайк
     */
    public boolean removeLike(int userId) {
        return likes.remove(userId);
    }
    
    /**
     * Получить количество лайков
     *
     * @return количество лайков
     */
    public int getLikesCount() {
        return likes.size();
    }
}