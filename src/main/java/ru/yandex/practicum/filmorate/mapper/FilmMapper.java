package ru.yandex.practicum.filmorate.mapper;

import java.util.*;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.*;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.util.ValidatorsDb;

import static ru.yandex.practicum.filmorate.util.Validators.isValidFilmReleaseDate;

@Component
@RequiredArgsConstructor
public class FilmMapper {
    private final ValidatorsDb validatorsDb;
    private final MpaService mpaService;
    private final GenreService genreService;

    public Film toEntity(FilmCreateDto filmCreateDto) {
        Film.FilmBuilder film = Film.builder()
                .name(filmCreateDto.getName())
                .description(filmCreateDto.getDescription())
                .duration(filmCreateDto.getDuration());

        if (!isValidFilmReleaseDate(filmCreateDto.getReleaseDate())) {
            LoggedException.throwNew(
                    new ValidationException(String.format("Дата создания фильма не может быть ранее 28 декабря 1895 г."
                                    + " Некорректная дата - %s",
                            filmCreateDto.getReleaseDate())), FilmService.class);
        }

        film.releaseDate(filmCreateDto.getReleaseDate());

        if (filmCreateDto.getMpa().isPresent()) {
            if (!validatorsDb.isValidMpa(filmCreateDto.getMpa().get().getId())) {
                LoggedException.throwNew(
                        new NotFoundException(String.format("MPA-рейтинг с id %d не существует.",
                                filmCreateDto.getMpa().get().getId())), FilmService.class);
            }
            film.mpa(mpaService.findById(filmCreateDto.getMpa().get().getId()));
        }

        List<GenreDto> genreDtoList = filmCreateDto.getGenres().orElse(new ArrayList<>());
        ArrayList<Genre> genreList = new ArrayList<>();

        if (!genreDtoList.isEmpty()) {
            for (GenreDto genreDto : filmCreateDto.getGenres().get()) {
                if (!validatorsDb.isValidGenre(genreDto.getId())) {
                    LoggedException.throwNew(
                            new NotFoundException(String.format("Жанр с id %d не существует.",
                                    genreDto.getId())), FilmService.class);
                }
            }
            genreDtoList.forEach(genreDto -> genreList.add(genreService.findById(genreDto.getId())));
        }
        film.genres(genreList);

        return film.build();
    }

    public Film toEntity(FilmUpdateDto filmUpdateDto) {
        Film.FilmBuilder filmBuilder = Film.builder()
                .id(filmUpdateDto.getId())
                .name(filmUpdateDto.getName().orElse(null))
                .duration(filmUpdateDto.getDuration().orElse(null))
                .description(filmUpdateDto.getDescription().orElse(null));

        if (Optional.ofNullable(filmUpdateDto.getReleaseDate()).isPresent()) {
            filmBuilder.releaseDate(filmUpdateDto.getReleaseDate());
        }

        if (filmUpdateDto.getGenres().isPresent()) {
            List<Genre> genresOfFilm = filmUpdateDto.getGenres().get().stream()
                    .mapToInt(GenreDto::getId)
                    .boxed()
                    .peek(genreId -> {
                        if (!validatorsDb.isValidGenre(genreId)) {
                            LoggedException.throwNew(
                                    new NotFoundException(String.format("Жанр с id %d не существует.",
                                            genreId)), FilmService.class);
                        }
                    })
                    .map(genreService::findById)
                    .toList();
            filmBuilder.genres(new ArrayList<>(genresOfFilm));
        }

        if (filmUpdateDto.getMpa().isPresent()) {
            filmBuilder.mpa(mpaService.findById(filmUpdateDto.getMpa().get().getId()));
        }

        return filmBuilder.build();
    }
}
