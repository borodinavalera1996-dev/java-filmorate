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
import ru.yandex.practicum.filmorate.dto.film.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MpaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MpaService mpaService;

    @InjectMocks
    private MpaController mpaController;

    private MpaDto gRating;
    private MpaDto pgRating;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mpaController).build();

        gRating = new MpaDto();
        gRating.setId(1L);
        gRating.setName("G");

        pgRating = new MpaDto();
        pgRating.setId(2L);
        pgRating.setName("PG");
    }

    @Test
    void getMpas_ShouldReturnListOfMpaRatings_WhenMpasExist() throws Exception {
        when(mpaService.findAll()).thenReturn(List.of(gRating, pgRating));

        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("G"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("PG"));

        verify(mpaService, times(1)).findAll();
    }

    @Test
    void getMpa_ShouldReturnMpaRating_WhenIdIsValid() throws Exception {
        long mpaId = 1L;
        when(mpaService.getMpa(mpaId)).thenReturn(gRating);

        mockMvc.perform(get("/mpa/{id}", mpaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("G"));

        verify(mpaService, times(1)).getMpa(mpaId);
    }
}
