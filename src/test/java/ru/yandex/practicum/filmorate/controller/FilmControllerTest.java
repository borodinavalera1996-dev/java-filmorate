package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.MpaDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    private FilmDto validFilmDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        validFilmDto = new FilmDto();
        validFilmDto.setId(1L);
        validFilmDto.setName("test");
        validFilmDto.setDescription("testtest");
        validFilmDto.setReleaseDate(LocalDate.of(2010, 7, 16));
        validFilmDto.setDuration(148L);
    }

    @Test
    void getFilms_ShouldReturnListOfFilms_WhenFilmsExist() throws Exception {
        when(filmService.findAll()).thenReturn(List.of(validFilmDto));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(validFilmDto.getId()))
                .andExpect(jsonPath("$[0].name").value(validFilmDto.getName()))
                .andExpect(jsonPath("$[0].duration").value(validFilmDto.getDuration()));

        verify(filmService, times(1)).findAll();
    }

    @Test
    void getFilm_ShouldReturnFilm_WhenIdIsValid() throws Exception {
        when(filmService.getFilmDto(1L)).thenReturn(validFilmDto);

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test"));

        verify(filmService, times(1)).getFilmDto(1L);
    }

    @Test
    void update_ShouldReturnUpdatedFilm_WhenRequestIsValid() throws Exception {
        UpdateFilmRequest updateRequest = new UpdateFilmRequest();
        updateRequest.setId(1L);
        updateRequest.setName("test Updated");
        updateRequest.setDuration(148L);
        updateRequest.setReleaseDate(LocalDate.of(2010, 7, 16));
        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(1L);
        updateRequest.setMpa(mpaDto);

        validFilmDto.setName("test Updated");
        when(filmService.update(any(UpdateFilmRequest.class))).thenReturn(validFilmDto);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test Updated"));

        verify(filmService, times(1)).update(any(UpdateFilmRequest.class));
    }

    @Test
    void setLike_ShouldReturnOk_WhenParametersAreValid() throws Exception {
        doNothing().when(filmService).setLike(anyLong(), anyLong());

        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk());

        verify(filmService, times(1)).setLike(1L, 2L);
    }

    @Test
    void deleteLike_ShouldReturnOk_WhenParametersAreValid() throws Exception {
        doNothing().when(filmService).deleteLike(anyLong(), anyLong());

        mockMvc.perform(delete("/films/1/like/2"))
                .andExpect(status().isOk());

        verify(filmService, times(1)).deleteLike(1L, 2L);
    }

    @Test
    void getTopFilms_ShouldReturnPopularFilmsList() throws Exception {
        when(filmService.getTopFilms(5L)).thenReturn(List.of(validFilmDto));

        mockMvc.perform(get("/films/popular?count=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(filmService, times(1)).getTopFilms(5L);
    }

    @Test
    void getTopFilms_ShouldUseDefaultCount_WhenCountParamIsMissing() throws Exception {
        when(filmService.getTopFilms(10L)).thenReturn(List.of(validFilmDto));

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk());

        verify(filmService, times(1)).getTopFilms(10L);
    }
}
