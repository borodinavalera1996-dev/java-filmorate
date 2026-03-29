package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.doNothing;
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
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, new HashSet<>());
        when(filmService.findAll()).thenReturn(List.of(mockFilm));

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"test\",\n" +
                        "    \"description\": \"testtest\",\n" +
                        "    \"releaseDate\": \"1999-12-06\",\n" +
                        "    \"duration\": 100\n" +
                        "}]"));
    }

    @Test
    public void shouldCreateFilm() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, new HashSet<>());
        when(filmService.create(mockFilm)).thenReturn(mockFilm);

        mockMvc.perform(post("/films")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"name\": \"test\",\n" +
                                "    \"description\": \"testtest\",\n" +
                                "    \"releaseDate\": \"1999-12-06\",\n" +
                                "    \"duration\": 100\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"test\",\n" +
                        "    \"description\": \"testtest\",\n" +
                        "    \"releaseDate\": \"1999-12-06\",\n" +
                        "    \"duration\": 100\n" +
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
                                "    \"duration\": 100\n" +
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
                                "    \"duration\": 100\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void shouldUpdateFilm() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, new HashSet<>());
        when(filmService.update(mockFilm)).thenReturn(mockFilm);

        mockMvc.perform(put("/films")
                        .content("{\n" +
                                "    \"id\": 1,\n" +
                                "    \"name\": \"test\",\n" +
                                "    \"description\": \"testtest\",\n" +
                                "    \"releaseDate\": \"1999-12-06\",\n" +
                                "    \"duration\": 100\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"test\",\n" +
                        "    \"description\": \"testtest\",\n" +
                        "    \"releaseDate\": \"1999-12-06\",\n" +
                        "    \"duration\": 100\n" +
                        "}"));
    }

    @Test
    public void shouldSetLike() throws Exception {
        doNothing().when(filmService).setLike(1L, 1L);

        mockMvc.perform(put("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteLike() throws Exception {
        doNothing().when(filmService).deleteLike(1L, 1L);

        mockMvc.perform(delete("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetTopFilms() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, new HashSet<>());
        when(filmService.getTopFilms(1)).thenReturn(List.of(mockFilm));

        mockMvc.perform(get("/films/popular?count=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"test\",\n" +
                        "    \"description\": \"testtest\",\n" +
                        "    \"releaseDate\": \"1999-12-06\",\n" +
                        "    \"duration\": 100\n" +
                        "}]"));
    }
}
