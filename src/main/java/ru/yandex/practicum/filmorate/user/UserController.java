package ru.yandex.practicum.filmorate.user;

import java.util.Collection;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    protected Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @NotNull @RequestBody UserCreateDto userCreateDto) {
        return userService.create(userCreateDto);
    }

    @PutMapping
    public User update(@RequestBody UserUpdateDto userUpdateDto) {
        return userService.update(userUpdateDto);
    }
}
