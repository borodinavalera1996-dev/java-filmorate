package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        long id = getNextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public Optional<User> get(long id) {
        if (users.containsKey(id))
            return Optional.of(users.get(id));
        return Optional.empty();
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователь с id = " + newUser.getId() + " не найден");
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }
        User oldUser = users.get(newUser.getId());
        if (!newUser.getEmail().isBlank()) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (!newUser.getLogin().isBlank()) {
            oldUser.setLogin(newUser.getLogin());
        }
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());
        return oldUser;
    }

    public void clear() {
        users.clear();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
