package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapperImpl;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapperImpl;
import ru.yandex.practicum.filmorate.model.FilmModel;
import ru.yandex.practicum.filmorate.model.UserModel;
import ru.yandex.practicum.filmorate.model.dto.restview.FilmRestView;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.impl.FilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserStorage;

public class FilmlServiceTest {
    private static FilmService service;
    private static FilmStorage filmStorage;
    private static UserStorage userStorage;
    private static UserModel userModel;
    private static FilmModel filmModel;
    private static FilmMapper filmMapper;

    @BeforeAll
    public static void initialize() {
        filmStorage = new FilmStorage();
        userStorage = new UserStorage();
        filmMapper = new FilmMapperImpl();
        UserMapper userMapper = new UserMapperImpl();
        service = new FilmServiceImpl(filmStorage, userStorage, filmMapper, userMapper);
    }

    @BeforeEach
    public void shouldBePreparedForTests() {
        filmStorage.deleteAll();
        userStorage.deleteAll();
        filmModel = filmStorage.save(FilmModel.builder()
                .name("Dance with seals")
                .description("Young girl and her friend seal try to fly to the moon and back.")
                .releaseDate(LocalDate.of(1990, 7, 15))
                .duration(115)
                .build());
        userModel = userStorage.save(UserModel.builder()
                .email("job@dog.ru")
                .login("kiss_all_human")
                .name("Василиса")
                .birthday(LocalDate.of(1950, 1, 1))
                .build());
    }

    @Test
    public void shouldAddLikeToFilmAndRemoveIt() {
        service.addLikeToFilmLikesSet(filmModel.getId(), userModel.getId());
        assertEquals(1, filmModel.getLikes().size());
        assertTrue(filmModel.getLikes().contains(userModel.getId()));
        service.removeLikeFromFilmLikesSet(filmModel.getId(), userModel.getId());
        assertEquals(0, filmModel.getLikes().size());
        assertFalse(filmModel.getLikes().contains(userModel.getId()));
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
        FilmModel anotherFilmModel = filmStorage.save(FilmModel.builder().build());
        service.addLikeToFilmLikesSet(anotherFilmModel.getId(), userModel.getId());
        List<FilmRestView> filmList = service.getMostLikedFilms(7);
        assertEquals(2, filmList.size());
        assertEquals(filmMapper.toRestView(anotherFilmModel), filmList.get(0));
    }

    @Test
    public void shouldReturnListOf10FilmsWithLikes() {
        Stream.iterate(1L, count -> count + 1).limit(100).forEach(count -> {
                    userStorage.save(UserModel.builder().email(count + "@r.r").build());
                    filmStorage.save(FilmModel.builder().build());
                });
        filmStorage.getAll().forEach(randomFilm ->
                userStorage.getAll().stream()
                        .limit(randomFilm.getId())
                        .forEach(randomUser -> service.addLikeToFilmLikesSet(randomFilm.getId(), randomUser.getId())));
        List<FilmRestView> filmList = service.getMostLikedFilms(10);
        assertEquals(101, filmList.get(0).getLikes().size());
    }

}