package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public List<User> getUsers() {
        log.info("Method getUsers was called.");
        return new ArrayList<>(userService.findAll());
    }

    @GetMapping(path = "/{id}")
    public User getUser(@PathVariable long id) {
        log.info("Method getUser was called.");
        return userService.getUser(id);
    }

    @PutMapping(path = "/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Method addFriend was called.");
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(path = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Method deleteFriend was called.");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping(path = "/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        log.info("Method getFriends was called.");
        return userService.getFriends(id);
    }

    @GetMapping(path = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Method getFriends was called.");
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.info("Method create User was called.");
        log.trace("With data: " + user.toString());
        User returnUser = userService.create(user);
        log.trace("With data: " + returnUser.toString());
        return returnUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Method create User was called.");
        log.trace("With data: " + newUser.toString());
        User returnUser = userService.update(newUser);
        log.trace("With data: " + returnUser.toString());
        return returnUser;
    }
}
