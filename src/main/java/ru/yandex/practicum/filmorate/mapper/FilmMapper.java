package ru.yandex.practicum.filmorate.mapper;

import java.util.ArrayList;
import java.util.Optional;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.*;
import ru.yandex.practicum.filmorate.exception.LoggedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.util.ValidatorsDb;

import static ru.yandex.practicum.filmorate.util.Validators.isValidFilmReleaseDate;

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

        if (filmCreateDto.getMpa().isPresent()) {
            if (validatorsDb.isValidMpa(filmCreateDto.getMpa().get().getId())) {
                film.mpa(mpaService.findById(filmCreateDto.getMpa().get().getId()));
            } else {
                LoggedException.throwNew(
                        new NotFoundException(String.format("MPA-рейтинг с id %d не существует.",
                                filmCreateDto.getMpa().get().getId())), FilmService.class);
            }
        }

        if (filmCreateDto.getGenres().isPresent()) {
            for (GenreDto genreDto : filmCreateDto.getGenres().get()) {
                if (!validatorsDb.isValidGenre(genreDto.getId())) {
                    LoggedException.throwNew(
                            new NotFoundException(String.format("Жанр с id %d не существует.",
                                    genreDto.getId())), FilmService.class);
                }
            }
            film.genres(new ArrayList<>(filmCreateDto.getGenres().get().stream()
                    .mapToInt(GenreDto::getId)
                    .boxed()
                    .map(genreService::findById)
                    .toList())
            );
        } else {
            film.genres(new ArrayList<>());
        }

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
//        if (filmUpdateDto.getMpa().isPresent()){
//            filmBuilder.mpa(mpaService.findById(filmUpdateDto.getMpa().get().getId()));
//        }
//        if (filmUpdateDto.getGenre().isPresent()) {
//            filmBuilder.genres(genreService.(filmUpdateDto.getGenre().get().stream().collect(Collectors.toSet())))
//        }
        return filmBuilder.build();
    }
}
