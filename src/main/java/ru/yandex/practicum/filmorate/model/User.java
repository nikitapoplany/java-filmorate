package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;

import lombok.*;

@Data
@Builder
public class User {
    @Getter
    private final Set<Integer> friends;
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}