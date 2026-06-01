package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component()
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_ALL_BY_IDS_QUERY = "SELECT id, name FROM genres WHERE id IN (";
    private static final String FIND_BY_ID_QUERY = "SELECT * " +
            "FROM genres g " +
            "WHERE g.id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> get(long id) {
        Optional<Genre> genre = findOne(FIND_BY_ID_QUERY, id);
        return genre;
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = FIND_ALL_BY_IDS_QUERY + placeholders + ") ORDER BY id";

        Object[] params = ids.toArray();

        return findMany(sql, params);
    }
}
