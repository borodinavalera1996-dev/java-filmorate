package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {
    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDuration(request.getDuration());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        if (request.getMpa() != null) {
            Mpa mpa = new Mpa();
            mpa.setId(request.getMpa());
            film.setMpa(mpa);
        }
        if (request.getGenres() != null) {
            Set<Genre> genres = request.getGenres().stream()
                    .map(genreId -> {
                        Genre genre = new Genre();
                        genre.setId(genreId);
                        return genre;
                    })
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        } else {
            film.setGenres(new HashSet<>());
        }
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDuration(film.getDuration());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setLikes(film.getLikes() != null ? film.getLikes() : new HashSet<>());

        if (film.getMpa() != null) {
            MpaDto mpaDto = new MpaDto();
            mpaDto.setId(film.getMpa().getId());
            mpaDto.setName(film.getMpa().getName());
            dto.setMpa(mpaDto);
        }

        if (film.getGenres() != null) {
            Set<GenreDto> genreDtos = film.getGenres().stream().map(genre -> {
                GenreDto gDto = new GenreDto();
                gDto.setId(genre.getId());
                gDto.setName(genre.getName());
                return gDto;
            }).collect(Collectors.toSet());
            dto.setGenres(genreDtos);
        } else {
            dto.setGenres(new HashSet<>());
        }
        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        film.setId(request.getId());
        film.setDescription(request.getDescription());
        film.setDuration(request.getDuration());
        film.setName(request.getName());
        film.setReleaseDate(request.getReleaseDate());
        if (request.getMpa() != null) {
            Mpa mpa = new Mpa();
            mpa.setId(request.getMpa().getId());
            film.setMpa(mpa);
        }
        if (request.getGenres() != null) {
            Set<Genre> genres = request.getGenres().stream()
                    .map(genreId -> {
                        Genre genre = new Genre();
                        genre.setId(genreId);
                        return genre;
                    })
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        } else {
            film.setGenres(new HashSet<>());
        }
        return film;
    }
}
