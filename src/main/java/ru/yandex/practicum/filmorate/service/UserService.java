package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private static void validate(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя было пустым");
            user.setName(user.getLogin());
        }
    }

    public User create(User user) {
        log.debug("create start with {}", user);
        validate(user);
        User res = userStorage.create(user);
        log.trace(res.toString());
        return res;
    }

    public User getUser(long id) {
        log.debug("getUser start with {}", id);
        Optional<User> res = userStorage.get(id);
        log.trace(res.toString());
        return res.orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public Collection<User> findAll() {
        log.debug("findAll start");
        Collection<User> all = userStorage.findAll();
        log.trace(all.toString());
        return all;
    }

    public User update(User newUser) {
        log.debug("update start with {}", newUser);
        validate(newUser);
        User res = userStorage.update(newUser);
        log.trace(res.toString());
        return res;
    }

    public User addFriend(long id, long friendId) {
        log.debug("addFriend start with id - {}, friendId - {}", id, friendId);
        User user = getUser(id);
        User friend = getUser(friendId);
        if (!user.equals(friend)) {
            user.getFriends().add(friend.getId());
            friend.getFriends().add(user.getId());
        }
        log.trace(user.toString());
        log.trace(friend.toString());
        return user;
    }

    public void deleteFriend(long id, long friendId) {
        log.debug("deleteFriend start with id - {}, friendId - {}", id, friendId);
        User user = getUser(id);
        User friend = getUser(friendId);
        if (!user.equals(friend)) {
            user.getFriends().remove(friend.getId());
            friend.getFriends().remove(user.getId());
        }
        log.trace(user.toString());
        log.trace(friend.toString());
    }

    public List<User> getFriends(long id) {
        log.debug("getFriends start with id - {}", id);
        User user = getUser(id);
        List<User> users = user.getFriends()
                .stream()
                .map(this::getUser)
                .toList();
        log.trace(users.toString());
        return users;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        log.debug("getCommonFriends start with id - {}, otherId - {}", id, otherId);
        User user = getUser(id);
        User other = getUser(otherId);
        List<Long> collect;
        if (!user.equals(other)) {
            collect = user.getFriends()
                    .stream()
                    .filter(user1 -> other.getFriends().contains(user1))
                    .toList();
        } else {
            collect = user.getFriends().stream().toList();
        }
        List<User> users = collect
                .stream()
                .map(this::getUser)
                .toList();
        log.trace(users.toString());
        return users;
    }

    public void clear() {
        userStorage.clear();
    }
}
