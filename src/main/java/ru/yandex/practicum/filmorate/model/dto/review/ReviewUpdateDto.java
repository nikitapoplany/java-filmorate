package ru.yandex.practicum.filmorate.model.dto.review;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;

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
