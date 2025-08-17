package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class User {
    private final Set<Integer> friends = new HashSet<>();
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}