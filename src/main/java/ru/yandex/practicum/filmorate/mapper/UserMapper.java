package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.model.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.model.User;

import static ru.yandex.practicum.filmorate.util.Validators.isValidString;

@Component
public class UserMapper {

    public User toEntity(UserCreateDto userCreateDto) {
        return User.builder()
                .name(isValidString(userCreateDto.getName()) ? userCreateDto.getName() : userCreateDto.getLogin())
                .login(userCreateDto.getLogin())
                .email(userCreateDto.getEmail())
                .birthday(userCreateDto.getBirthday())
                .build();
    }

    public User toEntity(UserUpdateDto userUpdateDto) {
        return User.builder()
                .id(userUpdateDto.getId())
                .email(userUpdateDto.getEmail().orElse(null))
                .name(isValidString(userUpdateDto.getName().orElse(null)) ?
                        userUpdateDto.getName().get() : userUpdateDto.getLogin()
                )
                .login(userUpdateDto.getLogin())
                .birthday(userUpdateDto.getBirthday())
                .build();
    }
}
