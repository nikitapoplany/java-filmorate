package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель данных рейтинга MPA (Motion Picture Association)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mpa {
    /**
     * Идентификатор рейтинга
     */
    private int id;

    /**
     * Название рейтинга
     */
    private String name;
}