package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmServiceTest {

    @Autowired
    public FilmService filmService;

    @Autowired
    public UserService userService;

    @AfterEach
    public void clearService() {
        filmService.clear();
    }

    @Test
    public void shouldReturnFilms() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmService.create(mockFilm);

        Collection<Film> all = filmService.findAll();
        assertEquals(all.size(), 1);
        assertIterableEquals(all, List.of(mockFilm));
    }

    @Test
    public void shouldCreateFilm() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        Film film = filmService.create(mockFilm);

        assertEquals(film, mockFilm);
    }

    @Test
    public void shouldCreateFilmWithOldReleaseDate() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1799, 12, 6), 100L, null);
        assertThrows(ValidationException.class, () -> filmService.create(mockFilm));
    }

    @Test
    public void shouldUpdateFilm() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        Film film = filmService.create(mockFilm);
        Film mockFilm1 = new Film(1L, "test1", "testtest1", LocalDate.of(1998, 12, 6), 100L, null);
        filmService.update(mockFilm1);
        assertEquals(film, mockFilm1);
    }

    @Test
    public void shouldSetLike() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmService.create(mockFilm);
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser);

        filmService.setLike(1L, 1L);

        Collection<Film> all = filmService.findAll();
        assertEquals(all.size(), 1);
        assertIterableEquals(all, List.of(mockFilm));
        assertEquals(all.iterator().next().getLikes().size(), 1);
    }

    @Test
    public void shouldSetLikeNotFound() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmService.create(mockFilm);
        assertThrows(NotFoundException.class, () -> filmService.setLike(1L, 1L));
    }

    @Test
    public void shouldDeleteLike() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmService.create(mockFilm);
        User mockUser = new User(1L, "test@fg.ru", "test", "name", LocalDate.of(1999, 12, 6), null);
        userService.create(mockUser);

        filmService.setLike(1L, 1L);

        filmService.deleteLike(1L, 1L);

        Collection<Film> all = filmService.findAll();
        assertEquals(all.size(), 1);
        assertIterableEquals(all, List.of(mockFilm));
        assertEquals(all.iterator().next().getLikes().size(), 0);
    }
}
