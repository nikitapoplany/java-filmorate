package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class})
public class GenreStorageTest {
    private final GenreDbStorage storage;

    @Test
    public void testFindById() {
        Genre genre = storage.findById(1);

        assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    public void testFindAll() {
        Set<Genre> genres = storage.findAll();

        assertThat(genres.size()).isEqualTo(6);
        assertThat(genres).allSatisfy(genre -> {
            assertThat(genre).hasFieldOrProperty("name");
            assertThat(genre).hasFieldOrProperty("id");
            assertThat(genre).hasNoNullFieldsOrProperties();
        });
    }

    @Test
    public void testFindGenreByFilmId() {
        List<Integer> genreIds = storage.findGenreByFilmId(1).stream().mapToInt(Genre::getId).boxed().toList();

        assertThat(genreIds).containsAll(List.of(2, 6));
    }

    @Test
    public void linkGenreToFilm() {
        storage.linkGenreToFilm(1, 3);
        List<Integer> genreIds = storage.findGenreByFilmId(1).stream().mapToInt(Genre::getId).boxed().toList();

        assertThat(genreIds).containsAll(List.of(2, 6, 3));
    }
}