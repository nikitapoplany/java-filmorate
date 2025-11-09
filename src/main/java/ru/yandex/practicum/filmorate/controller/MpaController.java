package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    @Autowired
    private final MpaService mpaService;

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable Integer id) {
        return mpaService.findById(id);
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        return mpaService.findAll();
    }
}
