package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") long count) {
        log.info("Method getTopFilms was called.");
        return filmService.getTopFilms(count);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>("{\"message\": \"Film not found: " + e.getMessage() + "\"}", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ValidationException.class, jakarta.validation.ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationException(Exception e) {
        return new ResponseEntity<>("{\"message\": \"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        // Fallback handler for any other unhandled exception
        return new ResponseEntity<>("Internal Server Error " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
