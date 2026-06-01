package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Collection<Genre> findAll();

    Optional<Genre> get(long id);

    List<Genre> findAllByIds(Set<Long> uniqueIds);
}
