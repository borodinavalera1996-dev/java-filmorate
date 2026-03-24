package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    public static final LocalDate DATE_OF_FIRST_FILM = LocalDate.of(1895, 12, 28);

    private FilmStorage filmStorage;
    private UserService userService;

    private static void validateFilm(Film film) {
        if (DATE_OF_FIRST_FILM.isAfter(film.getReleaseDate())) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
    }

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> findAll() {
        log.debug("findAll start");
        Collection<Film> all = filmStorage.findAll();
        log.trace(all.toString());
        return all;
    }

    public Film create(Film film) {
        log.debug("create start with {}", film);
        validateFilm(film);
        Film res = filmStorage.create(film);
        log.trace(res.toString());
        return res;
    }

    public Film update(Film newFilm) {
        log.debug("update start with {}", newFilm);
        validateFilm(newFilm);
        Film res = filmStorage.update(newFilm);
        log.trace(res.toString());
        return res;
    }

    public Film getFilm(long id) {
        log.debug("getFilm start with {}", id);
        Optional<Film> res = filmStorage.get(id);
        log.trace(res.toString());
        return res.orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public void setLike(long id, long userId) {
        log.debug("setLike start with id - {}, userId - {}", id, userId);
        userService.getUser(userId);
        getFilm(id);
        filmStorage.setLike(id, userId);
        log.debug("setLike finish with id - {}, userId - {}", id, userId);
    }

    public void deleteLike(long id, long userId) {
        log.debug("deleteLike start with id - {}, userId - {}", id, userId);
        userService.getUser(userId);
        getFilm(id);
        filmStorage.deleteLike(id, userId);
        log.debug("deleteLike finish with id - {}, userId - {}", id, userId);
    }

    public List<Film> getTopFilms(long count) {
        log.debug("getTopFilms start with count - {}", count);
        if (count > 0) {
            List<Film> topFilms = filmStorage.getTopFilms(count);
            log.trace(topFilms.toString());
            return topFilms;
        }
        return Collections.EMPTY_LIST;
    }

    public void clear() {
        filmStorage.clear();
    }
}
