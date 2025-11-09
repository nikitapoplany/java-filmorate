package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс хранилища рейтингов MPA
 */
public interface MpaStorage {
    /**
     * Получение списка всех рейтингов MPA
     *
     * @return список рейтингов
     */
    List<Mpa> getAllMpa();

    /**
     * Получение рейтинга MPA по идентификатору
     *
     * @param id идентификатор рейтинга
     * @return Optional, содержащий рейтинг, или пустой Optional, если рейтинг не найден
     */
    Optional<Mpa> getMpaById(int id);
}