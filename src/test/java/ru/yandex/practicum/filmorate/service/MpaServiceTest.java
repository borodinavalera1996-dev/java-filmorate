package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dto.film.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MpaServiceTest {

    private MpaService mpaService;

    @Mock
    private MpaStorage mpaStorage;

    private Mpa gRating;
    private Mpa pgRating;

    @BeforeEach
    void setUp() {
        // Явная инициализация из-за наличия двух конструкторов в тестируемом сервисе
        mpaService = new MpaService(mpaStorage);

        gRating = new Mpa();
        gRating.setId(1L);
        gRating.setName("G");

        pgRating = new Mpa();
        pgRating.setId(2L);
        pgRating.setName("PG");
    }

    @Test
    void findAll_ShouldReturnMappedMpaDtos() {
        when(mpaStorage.findAll()).thenReturn(List.of(gRating, pgRating));

        Collection<MpaDto> result = mpaService.findAll();

        assertThat(result).hasSize(2);
        verify(mpaStorage, times(1)).findAll();
    }

    @Test
    void getMpa_ShouldReturnMpaDto_WhenIdExists() {
        when(mpaStorage.get(1L)).thenReturn(Optional.of(gRating));

        MpaDto result = mpaService.getMpa(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("G");
        verify(mpaStorage, times(1)).get(1L);
    }

    @Test
    void getMpa_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        when(mpaStorage.get(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mpaService.getMpa(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("МПА не найден с ID: 999");

        verify(mpaStorage, times(1)).get(999L);
    }
}
