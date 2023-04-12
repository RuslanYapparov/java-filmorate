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
import ru.yandex.practicum.filmorate.model.dto.restcommand.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.dto.restcommand.UserRestCommand;
import ru.yandex.practicum.filmorate.model.FilmModel;
import ru.yandex.practicum.filmorate.model.UserModel;

import javax.validation.Validator;
import javax.validation.Validation;
import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

public class ValidatorTest {
    private static Validator validator;
    private static UserModel userModel;
    private static FilmModel filmModel;
    private static Set<ConstraintViolation<UserRestCommand>> userViolations;
    private static Set<ConstraintViolation<FilmRestCommand>> filmViolations;

    @BeforeAll
    public static void initialize() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    public void makeValidObjects() {
        userModel = UserModel.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        filmModel = FilmModel.builder()
                .id(0)
                .name("Whores & Whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
    }

    @Test
    public void shouldCheckValidObjectsWithoutViolations() {
        userViolations = validator.validate(createCommandObjectForTest(userModel));
        filmViolations = validator.validate(createCommandObjectForTest(filmModel));
        assertTrue(userViolations.isEmpty());
        assertTrue(filmViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckObjectsWithNegativeId() {
        userModel = userModel.toBuilder()
                .id(-7)
                .build();
        filmModel = FilmModel.builder()
                .id(-7)
                .build();
        userViolations = validator.validate(createCommandObjectForTest(userModel));
        filmViolations = validator.validate(createCommandObjectForTest(filmModel));
        assertFalse(userViolations.isEmpty());
        assertFalse(filmViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "\r", "\t", "\n", "sosiska", "@", "yahoo kiss@tu.bg", "ru\\m@tk.g", "177" })
    @NullAndEmptySource
    public void shouldProduceViolationsWhenCheckUserWithInvalidEmail(String email) {
        userModel = userModel.toBuilder()
                .email(email)
                .build();
        userViolations = validator.validate(createCommandObjectForTest(userModel));
        System.out.println(userViolations);
        assertFalse(userViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "\r", "\t", "\n", "sosiska ru" })
    @NullAndEmptySource
    public void shouldProduceViolationsWhenCheckUserWithInvalidLogin(String login) {
        userModel = userModel.toBuilder()
                .login(login)
                .build();
        userViolations = validator.validate(createCommandObjectForTest(userModel));
        assertFalse(userViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckUserWithInvalidBirtdate() {
        userModel = userModel.toBuilder()
                .birthday(LocalDate.of(2025, 11, 9))
                .build();
        userViolations = validator.validate(createCommandObjectForTest(userModel));
        assertFalse(userViolations.isEmpty());
        userModel = UserModel.builder()
                .birthday(null)
                .build();
        userViolations = validator.validate(createCommandObjectForTest(userModel));
        assertFalse(userViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "\r", "\t", "\n" })
    @NullAndEmptySource
    public void shouldProduceViolationsWhenCheckFilmWithInvalidName(String name) {
        filmModel = filmModel.toBuilder()
                .name(name)
                .build();
        filmViolations = validator.validate(createCommandObjectForTest(filmModel));
        assertFalse(filmViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Adventures of women in whales world. Everybody knows that whales are very big and " +
            "fairy creatures and can live in two worlds at the same time - our world and their world. " +
            "What if some whores can visit it too?" })
    @NullSource
    public void shouldProduceViolationsWhenCheckFilmWithInvalidDescription(String description) {
        filmModel = filmModel.toBuilder()
                .description(description)
                .build();
        filmViolations = validator.validate(createCommandObjectForTest(filmModel));
        assertFalse(filmViolations.isEmpty());
    }

    @Test
    public void shouldProduceViolationsWhenCheckFilmWithInvalidReleaseDate() {
        filmModel = filmModel.toBuilder()
                .releaseDate(null)
                .build();
        filmViolations = validator.validate(createCommandObjectForTest(filmModel));
        assertFalse(filmViolations.isEmpty());
        filmModel = filmModel.toBuilder()
                .releaseDate(LocalDate.of(2026, 12, 12))
                .build();
        filmViolations = validator.validate(createCommandObjectForTest(filmModel));
        assertFalse(filmViolations.isEmpty());
        filmModel = filmModel.toBuilder()
                .releaseDate(LocalDate.of(1890, 12, 12))
                .build();
        filmViolations = validator.validate(createCommandObjectForTest(filmModel));
        assertFalse(filmViolations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -7 })
    public void shouldProduceViolationsWhenCheckFilmWithInvalidDuration(int duration) {
        filmModel = filmModel.toBuilder()
                .duration(duration)
                .build();
        filmViolations = validator.validate(createCommandObjectForTest(filmModel));
        assertFalse(filmViolations.isEmpty());
    }

    private UserRestCommand createCommandObjectForTest(UserModel userModel) {
        UserRestCommand command = new UserRestCommand();
        command.setId(userModel.getId());
        command.setEmail(userModel.getEmail());
        command.setLogin(userModel.getLogin());
        command.setName(userModel.getName());
        command.setBirthday(userModel.getBirthday());
        command.setFriends(userModel.getFriends());
        return command;
    }

    private FilmRestCommand createCommandObjectForTest(FilmModel filmModel) {
        FilmRestCommand command = new FilmRestCommand();
        command.setId(filmModel.getId());
        command.setName(filmModel.getName());
        command.setDescription(filmModel.getDescription());
        command.setReleaseDate(filmModel.getReleaseDate());
        command.setDuration(filmModel.getDuration());
        command.setLikes(filmModel.getLikes());
        return command;
    }

}