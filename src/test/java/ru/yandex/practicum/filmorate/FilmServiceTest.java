package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.FilmRestCommand;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.impl.FilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserStorage;

public class FilmServiceTest {
    private static FilmService service;
    private static FilmStorage filmStorage;
    private static UserStorage userStorage;
    private static User user;
    private static Film film;

    @BeforeAll
    public static void initialize() {
        filmStorage = new FilmStorage();
        userStorage = new UserStorage();
        service = new FilmServiceImpl(filmStorage, userStorage);
    }

    @BeforeEach
    public void shouldBePreparedForTests() {
        filmStorage.deleteAll();
        userStorage.deleteAll();
        film = filmStorage.save(Film.builder()
                .name("Dance with seals")
                .description("Young girl and her friend seal try to fly to the moon and back.")
                .releaseDate(LocalDate.of(1990, 7, 15))
                .duration(115)
                .build());
        user = userStorage.save(User.builder()
                .email("job@dog.ru")
                .login("kiss_all_human")
                .name("Василиса")
                .birthday(LocalDate.of(1950, 1, 1))
                .build());
    }

    @Test
    public void shouldAddLikeToFilmAndRemoveIt() {
        service.addLikeToFilmLikesSet(film.getId(), user.getId());
        assertEquals(1, film.getLikes().size());
        assertTrue(film.getLikes().contains(user.getId()));
        service.removeLikeFromFilmLikesSet(film.getId(), user.getId());
        assertEquals(0, film.getLikes().size());
        assertFalse(film.getLikes().contains(user.getId()));
    }

    @Test
    public void shouldThrowExceptionWhenComeIncorrectId() {
        assertThrows(ObjectNotFoundInStorageException.class,
                () -> service.addLikeToFilmLikesSet(1, 7));
        assertThrows(ObjectNotFoundInStorageException.class,
                () -> service.removeLikeFromFilmLikesSet(0, 1));
    }

    @Test
    public void shouldThrowExceptionWhenComeIncorrectCountValueToMethodWithSort() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getMostLikedFilms(-1));
    }

    @Test
    public void shouldReturnListWithLikedFilms() {
        Film anotherFilm = filmStorage.save(Film.builder().build());
        service.addLikeToFilmLikesSet(anotherFilm.getId(), user.getId());
        List<FilmRestCommand> filmList = service.getMostLikedFilms(7);
        assertEquals(2, filmList.size());
        assertEquals(anotherFilm, filmList.get(0).convertToDomainObject());
    }

    @Test
    public void shouldReturnListOf10FilmsWithLikes() {
        Stream.iterate(1L, count -> count + 1).limit(100).forEach(count -> {
                    userStorage.save(User.builder().email(count + "@r.r").build());
                    filmStorage.save(Film.builder().build());
                });
        filmStorage.getAll().forEach(randomFilm ->
                userStorage.getAll().stream()
                        .limit(randomFilm.getId())
                        .forEach(randomUser -> service.addLikeToFilmLikesSet(randomFilm.getId(), randomUser.getId())));
        List<FilmRestCommand> filmList = service.getMostLikedFilms(10);
        assertEquals(101, filmList.get(0).convertToDomainObject().getLikes().size());
    }

}