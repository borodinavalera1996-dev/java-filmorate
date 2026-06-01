package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class FilmDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FilmDbStorage filmStorage;

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getLong("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        return film;
    };

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM film_likes");
        jdbcTemplate.execute("DELETE FROM film_genres");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM mpas");
        jdbcTemplate.execute("DELETE FROM genres");

        jdbcTemplate.execute("INSERT INTO mpas (id, name) VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13')");
        jdbcTemplate.execute("INSERT INTO genres (id, name) VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм')");

        filmStorage = new FilmDbStorage(jdbcTemplate, filmRowMapper);
    }

    @Test
    void create_ShouldSaveFilmWithGenres() {
        Film film = createValidFilm("Inception", 1L);
        Genre genre = new Genre();
        genre.setId(1L);
        film.setGenres(List.of(genre));

        Film createdFilm = filmStorage.create(film);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isPositive();

        Optional<Film> savedFilm = filmStorage.get(createdFilm.getId());
        assertThat(savedFilm).isPresent();
        assertThat(savedFilm.get().getName()).isEqualTo("Inception");
        assertThat(savedFilm.get().getGenres()).hasSize(1);
    }

    @Test
    void update_ShouldModifyFilmFieldsAndGenres() {
        Film film = filmStorage.create(createValidFilm("Avatar", 1L));

        film.setName("Avatar: Way of Water");
        Genre newGenre = new Genre();
        newGenre.setId(2L);
        film.setGenres(List.of(newGenre));

        Film updatedFilm = filmStorage.update(film);
        Optional<Film> dbFilm = filmStorage.get(updatedFilm.getId());

        assertThat(dbFilm).isPresent();
        assertThat(dbFilm.get().getName()).isEqualTo("Avatar: Way of Water");
        assertThat(dbFilm.get().getGenres().get(0).getId()).isEqualTo(2L);
    }

    @Test
    void get_ShouldReturnFilmWithGenresAndLikes() {
        Film film = filmStorage.create(createValidFilm("The Matrix", 1L));
        filmStorage.setLike(film.getId(), 10L);

        Optional<Film> foundFilm = filmStorage.get(film.getId());

        assertThat(foundFilm).isPresent();
        assertThat(foundFilm.get().getLikes()).contains(10L);
    }

    @Test
    void findAll_ShouldReturnAllFilmsWithCorrectOrderAndGenres() {
        Film film1 = createValidFilm("Film A", 1L);
        Film film2 = createValidFilm("Film B", 2L);

        filmStorage.create(film1);
        filmStorage.create(film2);

        Collection<Film> films = filmStorage.findAll();

        assertThat(films).hasSize(2);
    }

    @Test
    void setLike_And_deleteLike_ShouldManageLikesCorrectly() {
        Film film = filmStorage.create(createValidFilm("Interstellar", 3L));
        long userId = 5L;

        filmStorage.setLike(film.getId(), userId);
        assertThat(filmStorage.get(film.getId()).get().getLikes()).contains(userId);

        filmStorage.deleteLike(film.getId(), userId);
        assertThat(filmStorage.get(film.getId()).get().getLikes()).isEmpty();
    }

    private Film createValidFilm(String name, Long mpaId) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Description of " + name);
        film.setReleaseDate(LocalDate.of(2010, 1, 1));
        film.setDuration(120L);

        Mpa mpa = new Mpa();
        mpa.setId(mpaId);
        film.setMpa(mpa);

        film.setGenres(new ArrayList<>());
        film.setLikes(new HashSet<>());
        return film;
    }
}
