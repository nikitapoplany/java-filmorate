package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;

public interface LikeStorage {
    void setLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    List<Integer> getLikesByFilmId(Integer filmId);
}
