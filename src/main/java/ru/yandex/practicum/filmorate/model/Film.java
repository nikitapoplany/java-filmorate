package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;

import lombok.*;

@Data
@Builder
@Getter
public class Film {

    public Film(Film other) {
        this.likes = other.likes;
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
        this.releaseDate = other.releaseDate;
        this.duration = other.duration;
    }

    private final Set<Integer> likes;
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
}