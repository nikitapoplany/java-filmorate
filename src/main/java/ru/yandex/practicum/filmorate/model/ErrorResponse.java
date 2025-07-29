package ru.yandex.practicum.filmorate.model;

import lombok.Value;

@Value
public class ErrorResponse {
    int statusCode;
    String message;
}
