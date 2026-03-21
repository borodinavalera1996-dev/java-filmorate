package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User getUser(long id) {
        return userStorage.get(id);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public User addFriend(long id, long friendId) {
        User user = userStorage.get(id);
        User friend = userStorage.get(friendId);
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        return user;
    }

    public void deleteFriend(long id, long friendId) {
        User user = userStorage.get(id);
        User friend = userStorage.get(friendId);
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    public List<User> getFriends(long id) {
        User user = userStorage.get(id);
        return user.getFriends().stream().map(friend -> userStorage.get(friend)).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long otherId) {
        User user = userStorage.get(id);
        User other = userStorage.get(otherId);
        List<Long> collect = user.getFriends()
                .stream()
                .filter(user1 -> other.getFriends().contains(user1))
                .toList();
        return collect.stream().map(friend -> userStorage.get(friend)).collect(Collectors.toList());
    }

    public void clear() {
        userStorage.clear();
    }
}
