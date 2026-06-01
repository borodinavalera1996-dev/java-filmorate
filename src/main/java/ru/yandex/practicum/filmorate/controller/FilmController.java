package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping()
    public List<FilmDto> getFilms() {
        log.info("Method getFilms was called.");
        return new ArrayList<>(filmService.findAll());
    }

    @GetMapping(path = "/{id}")
    public FilmDto getFilm(@PathVariable long id) {
        log.info("Method getFilm was called.");
        return filmService.getFilmDto(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmRequest film) {
        log.info("Method create Film was called.");
        log.trace("With data: " + film.toString());
        FilmDto returnFilm = filmService.create(film);
        log.trace("With data: " + returnFilm.toString());
        return returnFilm;
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest newFilm) {
        log.info("Method update Film was called.");
        log.trace("With data: " + newFilm.toString());
        FilmDto returnFilm = filmService.update(newFilm);
        log.trace("With data: " + returnFilm.toString());
        return returnFilm;
    }

    @PutMapping(path = "{id}/like/{userId}")
    public void setLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Method setLike was called.");
        filmService.setLike(id, userId);
    }

    @DeleteMapping(path = "{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Method deleteLike was called.");
        filmService.deleteLike(id, userId);
    }

    @GetMapping(path = "/popular")
    public List<FilmDto> getTopFilms(@RequestParam(defaultValue = "10") long count) {
        log.info("Method getTopFilms was called.");
        return filmService.getTopFilms(count);
    }
}
