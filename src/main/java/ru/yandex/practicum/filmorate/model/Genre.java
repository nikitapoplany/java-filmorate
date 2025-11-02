package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель данных жанра фильма
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    /**
     * Идентификатор жанра
     */
    private int id;

    /**
     * Название жанра
     */
    private String name;
}