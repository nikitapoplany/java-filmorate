package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.*;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.film.GenreDto;

@Data
@Builder
public class Film {
    private final Set<Integer> likes = new HashSet<>();
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private LinkedHashSet<GenreDto> genres;
    private Mpa mpa;
}