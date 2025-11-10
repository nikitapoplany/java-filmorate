package ru.yandex.practicum.filmorate.service;

import java.util.List;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.util.ValidatorsDb;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeDbStorage likeStorage;
    private final ValidatorsDb validatorsDb;

    public void addLike(Integer filmId, Integer userId) {
        if (validatorsDb.isExistingLike(filmId, userId)) {
            LoggedException.throwNew(
                    new ValidationException(
                            String.format("Пользователь id %d уже поставил лайк фильму id %d", userId, filmId)), getClass());
        }
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (!validatorsDb.isExistingLike(filmId, userId)) {
            LoggedException.throwNew(
                    new NotFoundException("Ошибка при удалении лайка. Пользователь не ставил лайк фильму."), getClass());
        }
        likeStorage.removeLike(filmId, userId);
    }

    public List<Integer> getLikesByFilmId(Integer filmId) {
        return likeStorage.getLikesByFilmId(filmId);
    }
}
