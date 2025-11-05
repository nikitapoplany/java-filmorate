package ru.yandex.practicum.filmorate.mapper;

import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.util.ValidatorsDb;

import static ru.yandex.practicum.filmorate.util.Validators.*;

@Component
public class FilmMapper {
    private final ValidatorsDb validatorsDb;
    private final MpaService mpaService;
    private final GenreService genreService;

    @Autowired
    public FilmMapper(ValidatorsDb validatorsDb, MpaService mpaService, GenreService genreService) {
        this.validatorsDb = validatorsDb;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

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

        if (!validatorsDb.isValidMpa(filmCreateDto.getMpa().getId())) {
            LoggedException.throwNew(
                    new NotFoundException(String.format("MPA-рейтинг с id %d не существует.",
                            filmCreateDto.getMpa().getId())), FilmService.class);
        }

        film.mpa(mpaService.findById(filmCreateDto.getMpa().getId()));

        if (Optional.ofNullable(filmCreateDto.getGenre()).isPresent()) {
            for (Integer genreId : filmCreateDto.getGenre()) {
                if (!validatorsDb.isValidGenre(genreId)) {
                    LoggedException.throwNew(
                            new NotFoundException(String.format("Жанр с id %d не существует.",
                                    genreId)), FilmService.class);
                }
            }
            film.genres(filmCreateDto.getGenre().stream().map(genreService::findById).collect(Collectors.toSet()));
        }

        return film.build();
    }

    public Film toEntity(FilmUpdateDto filmUpdateDto) {
        return Film.builder()
                .id(filmUpdateDto.getId())
                .name(filmUpdateDto.getName().orElse(null))
                .duration(filmUpdateDto.getDuration().orElse(null))
                .description(filmUpdateDto.getDescription().orElse(null))
                .releaseDate(filmUpdateDto.getReleaseDate())
                .mpa(mpaService.findById(filmUpdateDto.getMpa().getId()))
                .genres(
                        filmUpdateDto.getGenre().stream()
                                .map(g -> genreService.findById(g.getId()))
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
