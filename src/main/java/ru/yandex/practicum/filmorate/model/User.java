package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

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
}