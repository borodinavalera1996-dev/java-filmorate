package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
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
    public List<Film> getFilms() {
        log.info("Method getFilms was called.");
        return new ArrayList<>(filmService.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Method create Film was called.");
        log.trace("With data: " + film.toString());
        Film returnFilm = filmService.create(film);
        log.trace("With data: " + returnFilm.toString());
        return returnFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Method update Film was called.");
        log.trace("With data: " + newFilm.toString());
        Film returnFilm = filmService.update(newFilm);
        log.trace("With data: " + returnFilm.toString());
        return returnFilm;
    }
}
