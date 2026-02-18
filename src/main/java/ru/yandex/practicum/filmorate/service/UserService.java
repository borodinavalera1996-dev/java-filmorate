package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        if (user.getLogin().contains(" "))
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());
        if (LocalDate.now().isBefore(user.getBirthday()))
            throw new ValidationException("Дата рождения не может быть в будущем");
        long id = getNextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getName() != null && !newUser.getName().isBlank()) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getBirthday() != null) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
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
