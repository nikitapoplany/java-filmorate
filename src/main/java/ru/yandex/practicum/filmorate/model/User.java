package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;

import lombok.*;

@Data
@Builder
@Getter
public class User {

    public User(User other) {
        this.friends = other.friends;
        this.id = other.id;
        this.email = other.email;
        this.login = other.login;
        this.name = other.name;
        this.birthday = other.birthday;
    }

    private final Set<Integer> friends;
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}