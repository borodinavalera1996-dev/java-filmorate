package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InMemoryFilmStorageTest {

    @Autowired
    public FilmStorage filmStorage;

    @AfterEach
    public void clearService() {
        filmStorage.clear();
    }

    @Test
    public void createFilm() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmStorage.create(mockFilm);

        Collection<Film> all = filmStorage.findAll();
        assertEquals(all.size(), 1);
        assertIterableEquals(all, List.of(mockFilm));
    }

    @Test
    public void updateFilm() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmStorage.create(mockFilm);
        Film updateFilm = new Film(1L, "test", "test789", LocalDate.of(1999, 12, 6), 100L, null);
        filmStorage.update(updateFilm);

        Collection<Film> all = filmStorage.findAll();
        assertEquals(all.size(), 1);
        assertIterableEquals(all, List.of(updateFilm));
    }

    @Test
    public void updateNotCreatedFilm() throws Exception {
        Film updateFilm = new Film(1L, "test", "test789", LocalDate.of(1999, 12, 6), 100L, null);
        assertThrows(NotFoundException.class, () -> filmStorage.update(updateFilm));
    }

    @Test
    public void updateFilmWithoutId() throws Exception {
        Film updateFilm = new Film(null, "test", "test789", LocalDate.of(1999, 12, 6), 100L, null);
        assertThrows(ValidationException.class, () -> filmStorage.update(updateFilm));
    }

    @Test
    public void findAll() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmStorage.create(mockFilm);
        Film mockFilm2 = new Film(2L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmStorage.create(mockFilm2);

        Collection<Film> all = filmStorage.findAll();
        assertEquals(all.size(), 2);
        assertIterableEquals(all, List.of(mockFilm, mockFilm2));
    }

    @Test
    public void setLike() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmStorage.create(mockFilm);
        filmStorage.setLike(mockFilm.getId(), 1L);

        Collection<Film> all = filmStorage.findAll();
        assertEquals(all.size(), 1);
        assertEquals(all.iterator().next().getLikes().size(), 1);
    }

    @Test
    public void setLikeWithException() throws Exception {
        assertThrows(NotFoundException.class, () -> filmStorage.setLike(1L, 1L));
    }

    @Test
    public void deleteLike() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmStorage.create(mockFilm);
        filmStorage.setLike(mockFilm.getId(), 1L);

        Collection<Film> all = filmStorage.findAll();
        assertEquals(all.size(), 1);
        assertEquals(all.iterator().next().getLikes().size(), 1);

        filmStorage.deleteLike(mockFilm.getId(), 1L);

        Collection<Film> all1 = filmStorage.findAll();
        assertEquals(all1.size(), 1);
        assertEquals(all1.iterator().next().getLikes().size(), 0);
    }

    @Test
    public void getTopFilms() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmStorage.create(mockFilm);
        filmStorage.setLike(mockFilm.getId(), 1L);
        Film mockFilm1 = new Film(2L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmStorage.create(mockFilm1);
        filmStorage.setLike(mockFilm1.getId(), 1L);
        filmStorage.setLike(mockFilm1.getId(), 2L);

        Collection<Film> all = filmStorage.getTopFilms(2);
        assertEquals(all.size(), 2);
        assertIterableEquals(all, List.of(mockFilm1, mockFilm));
    }

    @Test
    public void getTop1Films() throws Exception {
        Film mockFilm = new Film(1L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmStorage.create(mockFilm);
        filmStorage.setLike(mockFilm.getId(), 1L);
        Film mockFilm1 = new Film(2L, "test", "testtest", LocalDate.of(1999, 12, 6), 100L, null);
        filmStorage.create(mockFilm1);
        filmStorage.setLike(mockFilm1.getId(), 1L);
        filmStorage.setLike(mockFilm1.getId(), 2L);

        Collection<Film> all = filmStorage.getTopFilms(1);
        assertEquals(all.size(), 1);
        assertIterableEquals(all, List.of(mockFilm1));
    }

}
