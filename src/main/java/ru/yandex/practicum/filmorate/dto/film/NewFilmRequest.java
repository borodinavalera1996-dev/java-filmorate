package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class NewFilmRequest {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Past
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private Long duration;
    @NotNull
    private Long mpa_id;
    private Set<Long> genres;

    @JsonSetter("mpa")
    public void setMpaFromObject(Map<String, Object> mpa) {
        if (mpa != null && mpa.containsKey("id")) {
            this.mpa_id = Long.valueOf(mpa.get("id").toString());
        }
    }

    @JsonSetter("genres")
    public void setGenresFromObjects(Set<Map<String, Object>> genresList) {
        if (genresList != null) {
            this.genres = genresList.stream()
                    .filter(g -> g.containsKey("id"))
                    .map(g -> Long.valueOf(g.get("id").toString()))
                    .collect(Collectors.toSet());
        }
    }
}
