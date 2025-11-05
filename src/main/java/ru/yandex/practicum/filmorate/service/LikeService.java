package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.util.Validators;

@Service
public class LikeService {

    @Autowired
    private LikeDbStorage likeStorage;

    public void addLike(Integer filmId, Integer userId) {
        if (Validators.isExistingLike(filmId, userId)) {
            LoggedException.throwNew(
                    new ValidationException(
                            String.format("Пользователь id %d уже поставил лайк фильму id %d", userId, filmId)), getClass());
        }
        likeStorage.setLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (!Validators.isExistingLike(filmId, userId)) {
            LoggedException.throwNew(
                    new NotFoundException("Ошибка при удалении лайка. Пользователь не ставил лайк фильму."), getClass());
        }
        likeStorage.removeLike(filmId, userId);
    }
}
