package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(LikeDbStorage.class)
public class LikeStorageTest {
    private final LikeDbStorage storage;

    private void assertLikes(Integer filmId, Integer ...expectedLikes){
        List<Integer> likes = storage.getLikesByFilmId(filmId);
        assertThat(likes).containsOnly(expectedLikes);
    }

    @Test
    public void testGetLikesByFilmId() {
        assertLikes(1, 1, 2);
    }

    @Test
    public void testSetLike(){
        assertLikes(1, 1, 2);
        storage.setLike(1, 3);
        assertLikes(1, 1, 2, 3);
    }

    @Test
    public void testRemoveLike() {
        assertLikes(1, 1, 2);
        storage.removeLike(1, 2);
        assertLikes(1, 1);
    }
}
