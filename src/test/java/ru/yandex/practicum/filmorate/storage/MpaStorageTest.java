package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

import org.junit.jupiter.api.Test;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class, MpaRowMapper.class})
public class MpaStorageTest {
    private final MpaDbStorage storage;

    @Test
    public void testFindById() {
        Mpa mpa = storage.findById(5);

        assertThat(mpa).hasFieldOrPropertyWithValue("id", 5);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "NC-17");
    }

    @Test
    public void testFindAll() {
        Set<Mpa> mpaSet = storage.findAll();

        assertThat(mpaSet.size()).isEqualTo(5);
        assertThat(mpaSet).allSatisfy(mpa -> {
            assertThat(mpa).hasFieldOrProperty("id");
            assertThat(mpa).hasFieldOrProperty("name");
        });
    }
}