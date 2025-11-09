package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация хранилища жанров в базе данных
 */
@Repository
@Primary
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre ORDER BY genre_id";
        log.debug("Получение списка всех жанров");
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            Genre genre = jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
            return Optional.ofNullable(genre);
        } catch (Exception e) {
            log.warn("Жанр с id {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getGenresByFilmId(int filmId) {
        String sql = "SELECT g.genre_id, g.name FROM genre g " +
                "JOIN film_genre fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";
        log.debug("Получение списка жанров для фильма с id {}", filmId);
        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    @Override
    public void addGenresToFilm(int filmId, List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        // Удаляем дубликаты жанров по id
        List<Genre> uniqueGenres = new ArrayList<>();
        for (Genre genre : genres) {
            boolean isDuplicate = false;
            for (Genre uniqueGenre : uniqueGenres) {
                if (uniqueGenre.getId() == genre.getId()) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                uniqueGenres.add(genre);
            }
        }

        // Проверяем существование жанров перед добавлением
        for (Genre genre : uniqueGenres) {
            // Проверяем, существует ли жанр с таким id
            String checkSql = "SELECT COUNT(*) FROM genre WHERE genre_id = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, genre.getId());
            if (count == null || count == 0) {
                throw new ru.yandex.practicum.filmorate.exception.NotFoundException("Жанр с id " + genre.getId() + " не найден");
            }
        }

        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : uniqueGenres) {
            jdbcTemplate.update(sql, filmId, genre.getId());
            log.debug("Добавлен жанр с id {} для фильма с id {}", genre.getId(), filmId);
        }
    }

    @Override
    public void deleteGenresFromFilm(int filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
        log.debug("Удалены все жанры для фильма с id {}", filmId);
    }

    /**
     * Маппинг строки результата запроса в объект Genre
     *
     * @param rs     результат запроса
     * @param rowNum номер строки
     * @return объект Genre
     * @throws SQLException при ошибке доступа к данным
     */
    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        );
    }
}