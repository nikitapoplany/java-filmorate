package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.filmorate.config.ControllerTest;
import ru.yandex.practicum.filmorate.model.User;

@ControllerTest
public class UserControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldFailOnInvalidInput_createUserTest() {
        User user = new User();
        webTestClient.post()
                .uri("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }
}


