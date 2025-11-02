package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

/**
 * Контроллер для работы с рейтингами MPA
 */
@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    /**
     * Получение списка всех рейтингов MPA
     *
     * @return список рейтингов
     */
    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("Получен запрос GET /mpa");
        return mpaService.getAllMpa();
    }

    /**
     * Получение рейтинга MPA по идентификатору
     *
     * @param id идентификатор рейтинга
     * @return рейтинг
     */
    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        log.info("Получен запрос GET /mpa/{}", id);
        return mpaService.getMpaById(id);
    }
}