package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Валидатор для проверки даты релиза фильма
 * Дата релиза не может быть раньше 28 декабря 1895 года
 */
public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(ReleaseDateConstraint constraintAnnotation) {
        // Инициализация не требуется
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        // Проверка на null выполняется отдельной аннотацией @NotNull
        if (releaseDate == null) {
            return true;
        }
        return !releaseDate.isBefore(MIN_RELEASE_DATE);
    }
}