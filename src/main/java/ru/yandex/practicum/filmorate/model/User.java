package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Integer id;

    @Email
    private String email;

    @NotNull
    @NotBlank
    private String login;
    private String name;

    @PastOrPresent
    private LocalDate birthday;
}