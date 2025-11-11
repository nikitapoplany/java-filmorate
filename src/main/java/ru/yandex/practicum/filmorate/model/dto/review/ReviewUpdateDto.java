package ru.yandex.practicum.filmorate.model.dto.review;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class ReviewUpdateDto {
    @NotNull
    @Positive
    private Integer reviewId;

    @Builder.Default
    private Optional<String> content = Optional.empty();

    @Builder.Default
    private Optional<Boolean> isPositive = Optional.empty();
}
