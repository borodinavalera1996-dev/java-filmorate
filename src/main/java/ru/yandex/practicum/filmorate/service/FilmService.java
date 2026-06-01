package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    public static final LocalDate DATE_OF_FIRST_FILM = LocalDate.of(1895, 12, 28);

    private FilmStorage filmStorage;
    private UserService userService;
    private MpaService mpaService;
    private GenreService genreService;

    private void validateFilm(Film film) {
        if (DATE_OF_FIRST_FILM.isAfter(film.getReleaseDate())) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
        if (film.getMpa() == null) {
            log.error("Рейтинг MPA должен быть установлен.");
            throw new NotFoundException("Рейтинг MPA должен быть установлен.");
        }
        Mpa mpaId = film.getMpa();
        mpaService.getMpa(mpaId.getId());

        if (film.getGenres() != null) {
            List<Long> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());

            List<Genre> validatedGenres = genreService.findAllByIds(genreIds);

            film.setGenres(List.copyOf(validatedGenres));
        }
    }

    @Autowired
    public FilmService(@Qualifier("filmDb") FilmStorage filmStorage, UserService userService,
                       MpaService mpaService, GenreService genreService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public Collection<FilmDto> findAll() {
        log.debug("findAll start");
        return filmStorage.findAll()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto create(NewFilmRequest request) {
        log.debug("create start with {}", request);
        Film film = FilmMapper.mapToFilm(request);
        validateFilm(film);
        Film res = filmStorage.create(film);
        log.trace(res.toString());
        return FilmMapper.mapToFilmDto(res);
    }

    public FilmDto update(UpdateFilmRequest newFilm) {
        log.debug("update start with {}", newFilm);
        Film oldFilm = getFilm(newFilm.getId());
        Film film = FilmMapper.updateFilmFields(oldFilm, newFilm);
        validateFilm(film);
        Film res = filmStorage.update(film);
        log.trace(res.toString());
        return FilmMapper.mapToFilmDto(res);
    }

    public FilmDto getFilmDto(long id) {
        log.debug("getFilm start with {}", id);
        Optional<Film> res = filmStorage.get(id);
        log.trace(res.toString());
        return FilmMapper.mapToFilmDto(res.orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден")));
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

    public List<FilmDto> getTopFilms(long count) {
        log.debug("getTopFilms start with count - {}", count);
        if (count > 0) {
            List<Film> topFilms = filmStorage.getTopFilms(count);
            log.trace(topFilms.toString());
            return topFilms.stream()
                    .map(FilmMapper::mapToFilmDto)
                    .collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    public void clear() {
        filmStorage.clear();
    }
}
