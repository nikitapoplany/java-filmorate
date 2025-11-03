package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

/**
 * Сервис для работы с рейтингами MPA
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    /**
     * Получение списка всех рейтингов MPA
     *
     * @return список рейтингов
     */
    public List<Mpa> getAllMpa() {
        log.info("Получение списка всех рейтингов MPA");
        return mpaStorage.getAllMpa();
    }

    /**
     * Получение рейтинга MPA по идентификатору
     *
     * @param id идентификатор рейтинга
     * @return рейтинг
     * @throws NotFoundException если рейтинг не найден
     */
    public Mpa getMpaById(int id) {
        log.info("Получение рейтинга MPA с id {}", id);
        return mpaStorage.getMpaById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id " + id + " не найден"));
    }
}