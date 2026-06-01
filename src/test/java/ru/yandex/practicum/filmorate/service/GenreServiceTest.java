package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dto.film.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    private GenreService genreService;

    @Mock
    private GenreStorage genreStorage;

    private Genre comedy;
    private Genre drama;

    @BeforeEach
    void setUp() {
        genreService = new GenreService(genreStorage);

        comedy = new Genre();
        comedy.setId(1L);
        comedy.setName("Комедия");

        drama = new Genre();
        drama.setId(2L);
        drama.setName("Драма");
    }

    @Test
    void findAll_ShouldReturnMappedGenreDtos() {
        when(genreStorage.findAll()).thenReturn(List.of(comedy, drama));

        Collection<GenreDto> result = genreService.findAll();

        assertThat(result).hasSize(2);
        verify(genreStorage, times(1)).findAll();
    }

    @Test
    void getGenre_ShouldReturnGenreDto_WhenIdExists() {
        when(genreStorage.get(1L)).thenReturn(Optional.of(comedy));

        GenreDto result = genreService.getGenre(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Комедия");
        verify(genreStorage, times(1)).get(1L);
    }

    @Test
    void getGenre_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        when(genreStorage.get(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.getGenre(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Жанр не найден с ID: 999");

        verify(genreStorage, times(1)).get(999L);
    }

    @Test
    void findAllByIds_ShouldReturnListOfGenres_WhenAllIdsExist() {
        List<Long> ids = List.of(1L, 2L);
        when(genreStorage.findAllByIds(anySet())).thenReturn(List.of(comedy, drama));

        List<Genre> result = genreService.findAllByIds(ids);

        assertThat(result).hasSize(2);
        verify(genreStorage, times(1)).findAllByIds(anySet());
    }

    @Test
    void findAllByIds_ShouldReturnEmptyList_WhenIdsCollectionIsNullOrEmpty() {
        List<Genre> resultNull = genreService.findAllByIds(null);
        List<Genre> resultEmpty = genreService.findAllByIds(Collections.emptyList());

        assertThat(resultNull).isEmpty();
        assertThat(resultEmpty).isEmpty();
        verify(genreStorage, never()).findAllByIds(anySet());
    }

    @Test
    void findAllByIds_ShouldThrowNotFoundException_WhenSomeIdsAreMissingInDb() {
        List<Long> ids = List.of(1L, 999L);
        when(genreStorage.findAllByIds(anySet())).thenReturn(List.of(comedy));

        assertThatThrownBy(() -> genreService.findAllByIds(ids))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Один или несколько жанров не найдены в базе данных.");

        verify(genreStorage, times(1)).findAllByIds(anySet());
    }
}
