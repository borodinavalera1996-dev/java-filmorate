package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MpaDto> getMpas() {
        log.info("Method getMpas was called.");
        return new ArrayList<>(mpaService.findAll());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MpaDto getMpa(@PathVariable long id) {
        log.info("Method getMpa was called.");
        return mpaService.getMpa(id);
    }
}
