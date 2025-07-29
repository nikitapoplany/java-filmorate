package ru.yandex.practicum.filmorate.dto.put;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class FilmPutDto {
    @NotBlank
    private String name;

    @Length(max = 200)
    private String description;

    private LocalDate releaseDate;

    @Positive
    private Integer duration;
}