package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Validator;
import javax.validation.Validation;
import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

public class ValidatorTest {
    private static Validator validator;
    private static User user;
    private static Film film;
    private static Set<ConstraintViolation<User>> userViolations;
    private static Set<ConstraintViolation<Film>> filmViolations;

    @BeforeAll
    public static void initialize() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldCheckValidObjectsWithoutViolations() {
        user = User.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        film = Film.builder()
                .id(0)
                .name("Whores & Whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        userViolations = validator.validate(user);
        filmViolations = validator.validate(film);
        assertTrue(userViolations.isEmpty());
        assertTrue(filmViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckObjectsWithNegativeId() {
        user = User.builder()
                .id(-7)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        film = Film.builder()
                .id(-7)
                .name("Whores & whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        userViolations = validator.validate(user);
        filmViolations = validator.validate(film);
        assertFalse(userViolations.isEmpty());
        assertFalse(filmViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckUserWithInvalidEmail() {
        user = User.builder()
                .id(0)
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        user = user.toBuilder()
                .email(" ")
                .build();
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        user = user.toBuilder()
                .email("sosiska")
                .build();
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        user = user.toBuilder()
                .email("")
                .build();
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        user = user.toBuilder()
                .email("\n\n\n")
                .build();
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckUserWithInvalidLogin() {
        user = User.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        user = user.toBuilder()
                .login("")
                .build();
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        user = user.toBuilder()
                .login("\r\n\t")
                .build();
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        user = user.toBuilder()
                .login("Mr. President")
                .build();
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckFilmWithInvalidName() {
        film = Film.builder()
                .id(0)
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        film = film.toBuilder()
                .name("")
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        film = film.toBuilder()
                .name("  ")
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        film = film.toBuilder()
                .name("\r\t\n")
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckFilmWithInvalidDescription() {
        film = Film.builder()
                .id(0)
                .name("Whores & whales")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        film = film.toBuilder()
                .description("Adventures of women in whales world. Everybody knows that whales are very big and " +
                        "fairy creatures and can live in two worlds at the same time - our world and their world. " +
                        "What if some whores can visit it too?")
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckFilmWithInvalidReleaseDate() {
        film = Film.builder()
                .id(0)
                .name("Whores and whales")
                .description("Adventures of women in whales world")
                .duration(127)
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        film = film.toBuilder()
                .releaseDate(LocalDate.of(2026, 12, 12))
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        film = film.toBuilder()
                .releaseDate(LocalDate.of(1890, 12, 12))
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckFilmWithInvalidDuration() {
        film = Film.builder()
                .id(0)
                .name("Whores and whales")
                .description("Adventures of women in whales world")
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        film = film.toBuilder()
                .duration(0)
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        film = film.toBuilder()
                .duration(-7)
                .build();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
    }

}