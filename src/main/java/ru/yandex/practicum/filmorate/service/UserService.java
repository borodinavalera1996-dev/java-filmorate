package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
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
    public UserService(@Qualifier("userDb") UserStorage userStorage) {
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

    public UserDto create(NewUserRequest request) {
        log.debug("create start with {}", request);
        User user = UserMapper.mapToUser(request);
        validate(user);
        user = userStorage.create(user);
        log.trace(user.toString());
        return UserMapper.mapToUserDto(user);
    }

    public UserDto getUser(long userId) {
        log.debug("getUser start with {}", userId);
        return userStorage.get(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    public Collection<UserDto> findAll() {
        log.debug("findAll start");
        return userStorage.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto update(UpdateUserRequest request) {
        User updatedUser = userStorage.get(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedUser = userStorage.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void addFriend(long id, long friendId) {
        log.debug("addFriend start with id - {}, friendId - {}", id, friendId);
        UserDto user = getUser(id);
        UserDto friend = getUser(friendId);
        if (!user.equals(friend)) {
            userStorage.addFriend(id, friendId);
        }
        log.trace(user.toString());
        log.trace(friend.toString());
    }

    public void deleteFriend(long id, long friendId) {
        log.debug("deleteFriend start with id - {}, friendId - {}", id, friendId);
        UserDto user = getUser(id);
        UserDto friend = getUser(friendId);
        if (!user.equals(friend)) {
            userStorage.deleteFriend(id, friendId);
        }
        log.trace(user.toString());
        log.trace(friend.toString());
    }

    public List<UserDto> getFriends(long id) {
        log.debug("getFriends start with id - {}", id);
        getUser(id);
        List<User> friends = userStorage.getFriends(id);
        log.trace(friends.toString());
        return friends
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getCommonFriends(long id, long otherId) {
        log.debug("getCommonFriends start with id - {}, otherId - {}", id, otherId);
        List<User> friends = userStorage.getCommonFriends(id, otherId);
        log.trace(friends.toString());
        return friends
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public void clear() {
        userStorage.clear();
    }
}
