package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        long id = getNextId();
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.error("Фильм с id = " + newFilm.getId() + " не найден");
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        Film oldFilm = films.get(newFilm.getId());
        if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank()) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        oldFilm.setDuration(newFilm.getDuration());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        return oldFilm;
    }

    @Override
    public Optional<Film> get(long id) {
        if (films.containsKey(id))
            return Optional.of(films.get(id));
        return Optional.empty();
    }

    @Override
    public void setLike(long id, long userId) {
        if (!films.containsKey(id))
            throw new NotFoundException();
        films.get(id).getLikes().add(userId);
    }

    @Override
    public void deleteLike(long id, long userId) {
        if (!films.containsKey(id) || !films.get(id).getLikes().contains(userId))
            throw new NotFoundException();
        films.get(id).getLikes().remove(userId);
    }

    @Override
    public List<Film> getTopFilms(long count) {
        return films.values().stream()
                .sorted(Comparator.comparingLong(o -> -o.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        films.clear();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
