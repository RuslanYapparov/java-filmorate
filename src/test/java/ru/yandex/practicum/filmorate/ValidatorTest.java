package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;
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
    private static Set<ConstraintViolation<UserRestCommand>> userViolations;
    private static Set<ConstraintViolation<FilmRestCommand>> filmViolations;

    @BeforeAll
    public static void initialize() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    public void makeValidObjects() {
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
    }

    @Test
    public void shouldCheckValidObjectsWithoutViolations() {
        userViolations = validator.validate(new UserRestCommand(user));
        filmViolations = validator.validate(new FilmRestCommand(film));
        assertTrue(userViolations.isEmpty());
        assertTrue(filmViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckObjectsWithNegativeId() {
        user = user.toBuilder()
                .id(-7)
                .build();
        film = Film.builder()
                .id(-7)
                .build();
        userViolations = validator.validate(new UserRestCommand(user));
        filmViolations = validator.validate(new FilmRestCommand(film));
        assertFalse(userViolations.isEmpty());
        assertFalse(filmViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "\r", "\t", "\n", "sosiska", "@", "yahoo kiss@tu.bg", "ru\\m@tk.g", "177" })
    @NullAndEmptySource
    public void shouldProduceViolationsWhenCheckUserWithInvalidEmail(String email) {
        user = user.toBuilder()
                .email(email)
                .build();
        userViolations = validator.validate(new UserRestCommand(user));
        assertFalse(userViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "\r", "\t", "\n", "sosiska ru" })
    @NullAndEmptySource
    public void shouldProduceViolationsWhenCheckUserWithInvalidLogin(String login) {
        user = user.toBuilder()
                .login(login)
                .build();
        userViolations = validator.validate(new UserRestCommand(user));
        assertFalse(userViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckUserWithInvalidBirtdate() {
        user = user.toBuilder()
                .birthday(LocalDate.of(2025, 11, 9))
                .build();
        userViolations = validator.validate(new UserRestCommand(user));
        assertFalse(userViolations.isEmpty());
        user = User.builder()
                .birthday(null)
                .build();
        userViolations = validator.validate(new UserRestCommand(user));
        assertFalse(userViolations.isEmpty());

    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "\r", "\t", "\n" })
    @NullAndEmptySource
    public void shouldProduceViolationsWhenCheckFilmWithInvalidName(String name) {
        film = film.toBuilder()
                .name(name)
                .build();
        filmViolations = validator.validate(new FilmRestCommand(film));
        assertFalse(filmViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Adventures of women in whales world. Everybody knows that whales are very big and " +
            "fairy creatures and can live in two worlds at the same time - our world and their world. " +
            "What if some whores can visit it too?" })
    @NullSource
    public void shouldProduceViolationsWhenCheckFilmWithInvalidDescription(String description) {
        film = film.toBuilder()
                .description(description)
                .build();
        filmViolations = validator.validate(new FilmRestCommand(film));
        assertFalse(filmViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckFilmWithInvalidReleaseDate() {
        film = film.toBuilder()
                .releaseDate(null)
                .build();
        filmViolations = validator.validate(new FilmRestCommand(film));
        assertFalse(filmViolations.isEmpty());
        film = film.toBuilder()
                .releaseDate(LocalDate.of(2026, 12, 12))
                .build();
        filmViolations = validator.validate(new FilmRestCommand(film));
        assertFalse(filmViolations.isEmpty());
        film = film.toBuilder()
                .releaseDate(LocalDate.of(1890, 12, 12))
                .build();
        filmViolations = validator.validate(new FilmRestCommand(film));
        assertFalse(filmViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -7 })
    public void shouldProduceViolationsWhenCheckFilmWithInvalidDuration(int duration) {
        film = film.toBuilder()
                .duration(duration)
                .build();
        filmViolations = validator.validate(new FilmRestCommand(film));
        assertFalse(filmViolations.isEmpty());
    }

}