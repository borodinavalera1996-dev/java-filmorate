package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest user) {
        log.info("Method create User was called.");
        log.trace("With data: " + user.toString());
        UserDto returnUser = userService.create(user);
        log.trace("With data: " + returnUser.toString());
        return returnUser;
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest newUser) {
        log.info("Method create User was called.");
        log.trace("With data: " + newUser.toString());
        UserDto returnUser = userService.update(newUser);
        log.trace("With data: " + returnUser.toString());
        return returnUser;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers() {
        log.info("Method getUsers was called.");
        return new ArrayList<>(userService.findAll());
    }

    @GetMapping(path = "/{id}")
    public UserDto getUser(@PathVariable long id) {
        log.info("Method getUser was called.");
        return userService.getUser(id);
    }

    @PutMapping(path = "/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Method addFriend was called.");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(path = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Method deleteFriend was called.");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping(path = "/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable long id) {
        log.info("Method getFriends was called.");
        return userService.getFriends(id);
    }

    @GetMapping(path = "/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Method getFriends was called.");
        return userService.getCommonFriends(id, otherId);
    }
}
