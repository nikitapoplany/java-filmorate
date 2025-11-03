package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Реализация хранилища фильмов в базе данных
 */
@Repository
@Primary
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM film";
        log.debug("Получение списка всех фильмов");
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    /**
     * Получение списка популярных фильмов
     * @param count количество фильмов
     * @return список популярных фильмов
     */
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, " +
                "COUNT(l.user_id) as like_count " +
                "FROM film f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY like_count DESC, f.film_id DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }


    @Override
    public Film addFilm(Film film) {
        // Проверяем существование рейтинга MPA перед добавлением фильма
        Optional<ru.yandex.practicum.filmorate.model.Mpa> mpaOptional = mpaStorage.getMpaById(film.getMpa().getId());
        if (mpaOptional.isEmpty()) {
            throw new NotFoundException("Рейтинг MPA с id " + film.getMpa().getId() + " не найден");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> values = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "mpa_id", film.getMpa().getId()
        );

        Number key = simpleJdbcInsert.executeAndReturnKey(values);
        film.setId(key.intValue());

        // Добавляем жанры фильма
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreStorage.addGenresToFilm(film.getId(), film.getGenres());
        }

        // Загружаем полную информацию о рейтинге MPA
        film.setMpa(mpaOptional.get());

        log.debug("Фильм успешно добавлен: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        // Проверяем существование рейтинга MPA перед обновлением фильма
        Optional<ru.yandex.practicum.filmorate.model.Mpa> mpaOptional = mpaStorage.getMpaById(film.getMpa().getId());
        if (mpaOptional.isEmpty()) {
            throw new NotFoundException("Рейтинг MPA с id " + film.getMpa().getId() + " не найден");
        }

        String sql = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (rowsAffected == 0) {
            log.warn("Фильм с id {} не найден", film.getId());
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        // Обновляем жанры фильма
        genreStorage.deleteGenresFromFilm(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreStorage.addGenresToFilm(film.getId(), film.getGenres());
        }

        // Обновляем лайки
        updateLikes(film);

        // Загружаем полную информацию о рейтинге MPA
        film.setMpa(mpaOptional.get());

        // Загружаем жанры
        film.setGenres(genreStorage.getGenresByFilmId(film.getId()));

        log.debug("Фильм успешно обновлен: {}", film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        String sql = "SELECT * FROM film WHERE film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
            if (film != null) {
                // Загружаем лайки
                loadLikes(film);
                // Загружаем жанры
                film.setGenres(genreStorage.getGenresByFilmId(id));
            }
            return Optional.ofNullable(film);
        } catch (Exception e) {
            log.warn("Фильм с id {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public boolean filmExists(int id) {
        String sql = "SELECT COUNT(*) FROM film WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    /**
     * Загрузка лайков фильма
     *
     * @param film фильм
     */
    private void loadLikes(Film film) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Integer> userIds = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("user_id"),
                film.getId());

        Set<Integer> likes = new HashSet<>(userIds);
        film.setLikes(likes);
    }

    /**
     * Обновление лайков фильма
     *
     * @param film фильм
     */
    private void updateLikes(Film film) {
        // Удаляем все текущие лайки
        String deleteSql = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());

        // Добавляем новые лайки
        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            String insertSql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
            for (Integer userId : film.getLikes()) {
                jdbcTemplate.update(insertSql, film.getId(), userId);
            }
        }
    }

    /**
     * Маппинг строки результата запроса в объект Film
     *
     * @param rs     результат запроса
     * @param rowNum номер строки
     * @return объект Film
     * @throws SQLException при ошибке доступа к данным
     */
    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        // Загружаем рейтинг MPA
        int mpaId = rs.getInt("mpa_id");
        film.setMpa(mpaStorage.getMpaById(mpaId)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id " + mpaId + " не найден")));

        // Загружаем лайки
        loadLikes(film);

        return film;
    }
}