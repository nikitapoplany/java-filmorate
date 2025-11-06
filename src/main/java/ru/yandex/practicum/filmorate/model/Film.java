package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.*;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Film {
    private final Set<Integer> likes = new HashSet<>();
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private ArrayList<Genre> genres;
    private Mpa mpa;
}