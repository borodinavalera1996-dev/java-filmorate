package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Test
    public void shouldReturnFilms() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), Duration.ofHours(1));
        when(filmService.findAll()).thenReturn(List.of(mockFilm));

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"test\",\n" +
                        "    \"description\": \"testtest\",\n" +
                        "    \"releaseDate\": \"1999-12-06\",\n" +
                        "    \"duration\": \"PT1H\"\n" +
                        "}]"));
    }

    @Test
    public void shouldCreateFilm() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), Duration.ofHours(1));
        when(filmService.create(mockFilm)).thenReturn(mockFilm);

        mockMvc.perform(post("/films")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"name\": \"test\",\n" +
                                "    \"description\": \"testtest\",\n" +
                                "    \"releaseDate\": \"1999-12-06\",\n" +
                                "    \"duration\": \"PT1H\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"test\",\n" +
                        "    \"description\": \"testtest\",\n" +
                        "    \"releaseDate\": \"1999-12-06\",\n" +
                        "    \"duration\": \"PT1H\"\n" +
                        "}"));
    }

    @Test
    public void shouldCreateFilmWithLongDescription() throws Exception {
        mockMvc.perform(post("/films")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"name\": \"test\",\n" +
                                "    \"description\": \"testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77\",\n" +
                                "    \"releaseDate\": \"1999-12-06\",\n" +
                                "    \"duration\": \"PT1H\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldCreateFilmWithWrongDate() throws Exception {
        mockMvc.perform(post("/films")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"name\": \"test\",\n" +
                                "    \"description\": \"testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77testtest77\",\n" +
                                "    \"releaseDate\": \"199912-06\",\n" +
                                "    \"duration\": \"PT1H\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldUpdateFilm() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), Duration.ofHours(1));
        when(filmService.update(mockFilm)).thenReturn(mockFilm);

        mockMvc.perform(put("/films")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"name\": \"test\",\n" +
                                "    \"description\": \"testtest\",\n" +
                                "    \"releaseDate\": \"1999-12-06\",\n" +
                                "    \"duration\": \"PT1H\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"test\",\n" +
                        "    \"description\": \"testtest\",\n" +
                        "    \"releaseDate\": \"1999-12-06\",\n" +
                        "    \"duration\": \"PT1H\"\n" +
                        "}"));
    }
}
