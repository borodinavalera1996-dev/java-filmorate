package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
