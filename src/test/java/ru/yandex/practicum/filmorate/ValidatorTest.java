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
import ru.yandex.practicum.filmorate.model.presentation.rest_command.*;
import ru.yandex.practicum.filmorate.model.service.Film;
import ru.yandex.practicum.filmorate.model.service.RatingMpa;
import ru.yandex.practicum.filmorate.model.service.User;

import javax.validation.Validator;
import javax.validation.Validation;
import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .friends(new HashSet<>())
                .build();
        film = Film.builder()
                .name("Whores & whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .rate((byte) 2)
                .rating(RatingMpa.R)
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .directors(new HashSet<>())
                .build();
    }

    @Test
    public void shouldCheckValidObjectsWithoutViolations() {
        userViolations = validator.validate(createCommandObjectForTest(user));
        filmViolations = validator.validate(createCommandObjectForTest(film));
        assertTrue(userViolations.isEmpty());
        assertTrue(filmViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckObjectsWithNegativeId() {
        user = user.toBuilder()
                .id(-7)
                .build();
        film = film.toBuilder()
                .id(-7)
                .build();
        userViolations = validator.validate(createCommandObjectForTest(user));
        filmViolations = validator.validate(createCommandObjectForTest(film));
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
        userViolations = validator.validate(createCommandObjectForTest(user));
        System.out.println(userViolations);
        assertFalse(userViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "\r", "\t", "\n", "sosiska ru" })
    @NullAndEmptySource
    public void shouldProduceViolationsWhenCheckUserWithInvalidLogin(String login) {
        user = user.toBuilder()
                .login(login)
                .build();
        userViolations = validator.validate(createCommandObjectForTest(user));
        assertFalse(userViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckUserWithInvalidBirtdate() {
        user = user.toBuilder()
                .birthday(LocalDate.of(2025, 11, 9))
                .build();
        userViolations = validator.validate(createCommandObjectForTest(user));
        assertFalse(userViolations.isEmpty());
        user = User.builder()
                .birthday(null)
                .build();
        userViolations = validator.validate(createCommandObjectForTest(user));
        assertFalse(userViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "\r", "\t", "\n" })
    @NullAndEmptySource
    public void shouldProduceViolationsWhenCheckFilmWithInvalidName(String name) {
        film = film.toBuilder()
                .name(name)
                .build();
        filmViolations = validator.validate(createCommandObjectForTest(film));
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
        filmViolations = validator.validate(createCommandObjectForTest(film));
        assertFalse(filmViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckFilmWithInvalidReleaseDate() {
        film = film.toBuilder()
                .releaseDate(null)
                .build();
        filmViolations = validator.validate(createCommandObjectForTest(film));
        assertFalse(filmViolations.isEmpty());
        film = film.toBuilder()
                .releaseDate(LocalDate.of(2026, 12, 12))    // Пришлось убрать аннотацию @Past
                .build();                // С поля releaseDate из-за теста в Postman, в котором есть фильм из будущего
        filmViolations = validator.validate(createCommandObjectForTest(film));
        assertTrue(filmViolations.isEmpty());
        film = film.toBuilder()
                .releaseDate(LocalDate.of(1890, 12, 12))
                .build();
        filmViolations = validator.validate(createCommandObjectForTest(film));
        assertFalse(filmViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -7 })
    public void shouldProduceViolationsWhenCheckFilmWithInvalidDuration(int duration) {
        film = film.toBuilder()
                .duration(duration)
                .build();
        filmViolations = validator.validate(createCommandObjectForTest(film));
        assertFalse(filmViolations.isEmpty());
    }

    private UserRestCommand createCommandObjectForTest(User user) {
        long id = user.getId();
        String email = user.getEmail();
        String login = user.getLogin();
        String name = user.getName();
        LocalDate birthday = user.getBirthday();
        Set<Long> friends = user.getFriends();
        return new UserRestCommand(id, email, login, name, birthday, friends);
    }

    private FilmRestCommand createCommandObjectForTest(Film film) {
        long id = film.getId();
        String name = film.getName();
        String description = film.getDescription();
        LocalDate releaseDate = film.getReleaseDate();
        int duration = film.getDuration();
        byte rate = film.getRate();
        RatingMpaRestCommand mpa = new RatingMpaRestCommand(film.getRating().getId());
        Set<Long> likes = film.getLikes();
        Set<GenreRestCommand> genres = film.getGenres().stream()
                .map(genre -> new GenreRestCommand(genre.getId()))
                .collect(Collectors.toSet());
        Set<DirectorRestCommand> directors = film.getDirectors().stream()
                .map(director -> new DirectorRestCommand(director.getId(), director.getName()))
                .collect(Collectors.toSet());
        return new FilmRestCommand(id, name, description, releaseDate, duration, rate, mpa, likes, genres, directors);
    }

}