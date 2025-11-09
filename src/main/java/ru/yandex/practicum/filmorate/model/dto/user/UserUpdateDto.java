package ru.yandex.practicum.filmorate.model.dto.user;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDto {
    @NotNull
    @Positive
    private Integer id;

    @Email
    private Optional<String> email;

    @NotBlank
    private String login;

    private Optional<String> name;

    @PastOrPresent
    private LocalDate birthday;
}