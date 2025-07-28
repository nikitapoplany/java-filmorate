package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.filmorate.config.ControllerTest;
import ru.yandex.practicum.filmorate.model.Film;

@ControllerTest
public class FilmControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldFailOnInvalidInput_createFilmTest(){
        Film film = new Film();
        webTestClient.post()
                .uri("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(film)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }
}


