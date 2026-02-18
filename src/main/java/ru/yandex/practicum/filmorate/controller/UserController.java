package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        return new ArrayList<>(userService.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        try {
            return userService.create(user);
        } catch (ValidationException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        try {
            return userService.update(newUser);
        } catch (ValidationException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
