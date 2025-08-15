package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;

import lombok.*;

@Data
@Builder
public class Film {
    @Getter
    private final Set<Integer> likes;

    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
}