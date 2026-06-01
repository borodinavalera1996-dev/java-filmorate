package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    private FilmService filmService;

    @Mock
    private FilmStorage filmStorage;
    @Mock
    private UserService userService;
    @Mock
    private MpaService mpaService;
    @Mock
    private GenreService genreService;

    private Film validFilm;
    private Mpa validMpa;

    @BeforeEach
    void setUp() {
        filmService = new FilmService(filmStorage, userService, mpaService, genreService);

        validMpa = new Mpa();
        validMpa.setId(1L);
        validMpa.setName("G");

        validFilm = new Film();
        validFilm.setId(1L);
        validFilm.setName("Inception");
        validFilm.setDescription("Dream theft movie");
        validFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        validFilm.setDuration(148L);
        validFilm.setMpa(validMpa);
        validFilm.setGenres(new ArrayList<>());
    }

    @Test
    void findAll_ShouldReturnMappedFilmDtos() {
        when(filmStorage.findAll()).thenReturn(List.of(validFilm));

        Collection<FilmDto> result = filmService.findAll();

        assertThat(result).hasSize(1);
        verify(filmStorage, times(1)).findAll();
    }

    @Test
    void create_ShouldSaveFilm_WhenFilmIsValid() {
        NewFilmRequest request = new NewFilmRequest();
        request.setName("Inception");
        request.setDescription("Dream theft movie");
        request.setReleaseDate(LocalDate.of(2010, 7, 16));
        request.setDuration(148L);
        request.setMpa(1L);

        validFilm.setId(0L);

        when(filmStorage.create(any(Film.class))).thenReturn(validFilm);

        FilmDto createdDto = filmService.create(request);

        assertThat(createdDto).isNotNull();
        verify(filmStorage, times(1)).create(any(Film.class));
    }

    @Test
    void create_ShouldThrowValidationException_WhenReleaseDateIsBeforeFirstFilm() {
        NewFilmRequest request = new NewFilmRequest();
        request.setName("Old Movie");
        request.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertThatThrownBy(() -> filmService.create(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Дата релиза — не раньше 28 декабря 1895 года.");

        verify(filmStorage, never()).create(any());
    }

    @Test
    void create_ShouldThrowNotFoundException_WhenMpaDoesNotExist() {
        NewFilmRequest request = new NewFilmRequest();
        request.setName("No MPA Movie");
        request.setReleaseDate(LocalDate.now());

        assertThatThrownBy(() -> filmService.create(request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getFilm_ShouldReturnFilm_WhenIdExists() {
        when(filmStorage.get(1L)).thenReturn(Optional.of(validFilm));

        Film result = filmService.getFilm(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Inception");
    }

    @Test
    void getFilm_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        when(filmStorage.get(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmService.getFilm(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Фильм с id 999 не найден");
    }

    @Test
    void setLike_ShouldCallStorage_WhenUserAndFilmExist() {
        when(filmStorage.get(1L)).thenReturn(Optional.of(validFilm));

        filmService.setLike(1L, 2L);

        verify(userService, times(1)).getUser(2L);
        verify(filmStorage, times(1)).setLike(1L, 2L);
    }

    @Test
    void deleteLike_ShouldCallStorage_WhenUserAndFilmExist() {
        when(filmStorage.get(1L)).thenReturn(Optional.of(validFilm));

        filmService.deleteLike(1L, 2L);

        verify(userService, times(1)).getUser(2L);
        verify(filmStorage, times(1)).deleteLike(1L, 2L);
    }

    @Test
    void getTopFilms_ShouldReturnPopularFilms_WhenCountIsPositive() {
        when(filmStorage.getTopFilms(5L)).thenReturn(List.of(validFilm));

        List<FilmDto> topFilms = filmService.getTopFilms(5L);

        assertThat(topFilms).hasSize(1);
        verify(filmStorage, times(1)).getTopFilms(5L);
    }

    @Test
    void getTopFilms_ShouldReturnEmptyList_WhenCountIsZeroOrNegative() {
        List<FilmDto> topFilms = filmService.getTopFilms(0L);

        assertThat(topFilms).isEmpty();
        verify(filmStorage, never()).getTopFilms(anyLong());
    }
}
