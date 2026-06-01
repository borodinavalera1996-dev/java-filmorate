package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.dto.film.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GenreControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreController genreController;

    private GenreDto comedyGenre;
    private GenreDto dramaGenre;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(genreController).build();

        comedyGenre = new GenreDto();
        comedyGenre.setId(1L);
        comedyGenre.setName("Комедия");

        dramaGenre = new GenreDto();
        dramaGenre.setId(2L);
        dramaGenre.setName("Драма");
    }

    @Test
    void getGenres_ShouldReturnListOfGenres_WhenGenresExist() throws Exception {
        when(genreService.findAll()).thenReturn(List.of(comedyGenre, dramaGenre));

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Комедия"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Драма"));

        verify(genreService, times(1)).findAll();
    }

    @Test
    void getGenre_ShouldReturnGenre_WhenIdIsValid() throws Exception {
        long genreId = 1L;
        when(genreService.getGenre(genreId)).thenReturn(comedyGenre);

        mockMvc.perform(get("/genres/{id}", genreId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Комедия"));

        verify(genreService, times(1)).getGenre(genreId);
    }
}
