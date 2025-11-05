package ru.yandex.practicum.filmorate.mapper;

import java.util.stream.Collectors;

import jakarta.validation.ValidationException;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import static ru.yandex.practicum.filmorate.util.Validators.isValidFilmReleaseDate;

public class FilmMapper {

    public static Film toEntity(FilmCreateDto filmCreateDto) {
        if (!isValidFilmReleaseDate(filmCreateDto.getReleaseDate())) {
            LoggedException.throwNew(
                    new ValidationException(String.format("Дата создания фильма не может быть ранее 28 декабря 1895 г."
                                                          + " Некорректная дата - %s",
                            filmCreateDto.getReleaseDate())), FilmService.class);
        }

        return Film.builder()
                .name(filmCreateDto.getName())
                .description(filmCreateDto.getDescription())
                .duration(filmCreateDto.getDuration())
                .releaseDate(filmCreateDto.getReleaseDate())
                .mpaId(filmCreateDto.getMpa().getId())
                .genreId(filmCreateDto.getGenre().stream().mapToInt(Genre::getId).boxed().collect(Collectors.toSet()))
                .build();
    }

    public static Film toEntity(FilmUpdateDto filmUpdateDto) {
        return Film.builder()
                .id(filmUpdateDto.getId())
                .name(filmUpdateDto.getName().orElse(null))
                .duration(filmUpdateDto.getDuration().orElse(null))
                .description(filmUpdateDto.getDescription().orElse(null))
                .releaseDate(filmUpdateDto.getReleaseDate())
                .mpaId(filmUpdateDto.getMpa().getId())
                .genreId(filmUpdateDto.getGenre().stream().mapToInt(Genre::getId).boxed().collect(Collectors.toSet()))
                .build();
    }
}
