package ru.yandex.practicum.filmorate.storage.interfaces;

public interface LikeStorage {
    void setLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);
}
