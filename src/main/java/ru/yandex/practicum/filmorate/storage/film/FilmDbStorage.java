package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("filmDb")
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT f.*, m.name AS mpa_name " +
            "FROM films f " +
            "LEFT JOIN mpas m ON f.mpa_id = m.id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, m.name AS mpa_name" +
            " FROM films f" +
            " LEFT JOIN mpas m ON f.mpa_id = m.id\n" +
            " WHERE f.id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, mpa_id, duration) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, mpa_id = ?, duration = ? WHERE id = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

    private static final String INSERT_LIKE_QUERY = "INSERT INTO film_likes(film_id, user_id) " +
            "VALUES (?, ?)";
    private static final String GET_TOP_FILMS_QUERY = "SELECT \n" +
            "    f.id, \n" +
            "    f.name, \n" +
            "    f.description, \n" +
            "    f.release_date, \n" +
            "    f.mpa_id, \n" +
            "    f.duration, \n" +
            "    COUNT(fl.user_id) AS likes_count, \n" +
            "    m.name AS mpa_name \n" +
            "FROM films f\n" +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id\n" +
            "LEFT JOIN mpas m ON f.mpa_id = m.id\n" +
            "GROUP BY f.id\n" +
            "ORDER BY likes_count DESC, f.id ASC\n" +
            "LIMIT ?;";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }


    @Override
    public Collection<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film create(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getDuration()
        );
        film.setId(id);
        saveGenres(id, film.getGenres());
        saveLikes(id, film.getLikes());
        return film;
    }

    @Override
    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getDuration(),
                film.getId()
        );
        saveGenres(film.getId(), film.getGenres());
        saveLikes(film.getId(), film.getLikes());
        return film;
    }

    @Override
    public Optional<Film> get(long id) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, id);
        if (film != null) {
            film.get().setGenres(getGenresByFilmId(id));
            film.get().setLikes(getLikesByFilmId(id));
        }
        return film;
    }

    public void saveGenres(Long filmId, Set<Genre> genreIds) {
        String sqlDelete = "DELETE FROM film_genres WHERE film_id = ?";
        jdbc.update(sqlDelete, filmId);

        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }

        String sqlInsert = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        List<Genre> genresList = new ArrayList<>(genreIds);
        jdbc.batchUpdate(sqlInsert, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, genresList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genresList.size();
            }
        });
    }

    public void saveLikes(Long filmId, Set<Long> likeIds) {
        String sqlDelete = "DELETE FROM film_likes WHERE film_id = ?";
        jdbc.update(sqlDelete, filmId);

        if (likeIds == null || likeIds.isEmpty()) {
            return;
        }

        String sqlInsert = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";

        List<Long> likesList = new ArrayList<>(likeIds);

        jdbc.batchUpdate(sqlInsert, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, likesList.get(i));
            }

            @Override
            public int getBatchSize() {
                return likesList.size();
            }
        });
    }

    private Set<Genre> getGenresByFilmId(Long filmId) {
        String sqlGenres = "SELECT fg.genre_id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?";

        return new HashSet<>(jdbc.query(sqlGenres, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId));
    }

    private Set<Long> getLikesByFilmId(Long filmId) {
        String sqlLikes = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbc.query(sqlLikes,
                (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }

    @Override
    public void setLike(long id, long userId) {
        update(INSERT_LIKE_QUERY,
                id,
                userId
        );
    }

    @Override
    public void deleteLike(long id, long userId) {
        update(DELETE_LIKE_QUERY,
                id,
                userId
        );
    }

    @Override
    public List<Film> getTopFilms(long count) {
        return findMany(GET_TOP_FILMS_QUERY, count);
    }

    @Override
    public void clear() {

    }
}
