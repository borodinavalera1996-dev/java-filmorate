package ru.yandex.practicum.filmorate.storage.film.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setMpa(getMpa(resultSet));
        film.setDuration(resultSet.getLong("duration"));

        Date releaseDate = resultSet.getDate("release_date");
        film.setReleaseDate(releaseDate.toLocalDate());
        return film;
    }

    private static Mpa getMpa(ResultSet resultSet) throws SQLException {
        long mpaId = resultSet.getLong("mpa_id");
        String mpaName = resultSet.getString("mpa_name");
        Mpa mpa = new Mpa();
        mpa.setId(mpaId);
        mpa.setName(mpaName);
        return mpa;
    }
}
