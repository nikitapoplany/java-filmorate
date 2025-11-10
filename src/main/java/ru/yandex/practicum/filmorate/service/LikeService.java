package ru.yandex.practicum.filmorate.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.util.Validators;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeDbStorage likeStorage;
    private final Validators validators;

    public void addLike(Integer filmId, Integer userId) {
        validators.validateLikeNotExists(filmId, userId, getClass());
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        validators.validateLikeExists(filmId, userId, getClass());
        likeStorage.removeLike(filmId, userId);
    }

    public List<Integer> getLikesByFilmId(Integer filmId) {
        return likeStorage.getLikesByFilmId(filmId);
    }
}
