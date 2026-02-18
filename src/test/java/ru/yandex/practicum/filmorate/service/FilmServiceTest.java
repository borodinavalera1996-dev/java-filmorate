package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

public class FilmServiceTest {

    @Autowired
    public FilmService filmService;

    @AfterEach
    public void clearService() {
        filmService.clear();
    }

    @Test
    public void shouldReturnFilms() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), Duration.ofHours(1));
        filmService.create(mockFilm);

        Collection<Film> all = filmService.findAll();
        assertEquals(all.size(), 1);
        assertIterableEquals(all, List.of(mockFilm));
    }

    @Test
    public void shouldCreateFilm() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), Duration.ofHours(1));
        Film film = filmService.create(mockFilm);

        assertEquals(film, mockFilm);
    }

    @Test
    public void shouldCreateFilmWithDurationIsNegative() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), Duration.ofHours(1).minusHours(3));
        assertThrows(ValidationException.class, () -> filmService.create(mockFilm));
    }

    @Test
    public void shouldCreateFilmWithOldReleaseDate() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1799, 12, 6), Duration.ofHours(1));
        assertThrows(ValidationException.class, () -> filmService.create(mockFilm));
    }

    @Test
    public void shouldUpdateFilm() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), Duration.ofHours(1));
        Film film = filmService.create(mockFilm);
        Film mockFilm1 = new Film(1L, "test1", "testtest1", LocalDate.of(1998, 12, 6), Duration.ofHours(1));
        filmService.update(mockFilm1);
        assertEquals(film, mockFilm1);
    }
}
