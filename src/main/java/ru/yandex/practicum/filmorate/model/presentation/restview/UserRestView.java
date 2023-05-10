package ru.yandex.practicum.filmorate.model.presentation.restview;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@EqualsAndHashCode
@AllArgsConstructor
@Builder   // Пока не нашел способ генерации UserMapperImpl с правильным созданием объекта без данной аннотации
@NoArgsConstructor
@Getter
public class UserRestView {
    @JsonProperty("id")
    private long id;
    @JsonProperty("email")
    private String email;
    @JsonProperty("login")
    private String login;
    @JsonProperty("name")
    private String name;
    @JsonProperty("birthday")
    private LocalDate birthday;
    @JsonProperty("friends")
    private Set<Long> friends;

}