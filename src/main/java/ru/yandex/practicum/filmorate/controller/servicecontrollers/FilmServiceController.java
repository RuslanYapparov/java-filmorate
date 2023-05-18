package ru.yandex.practicum.filmorate.controller.servicecontrollers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.service.*;
import ru.yandex.practicum.filmorate.model.presentation.restview.FilmRestView;
import ru.yandex.practicum.filmorate.model.presentation.restview.GenreRestView;
import ru.yandex.practicum.filmorate.model.presentation.restview.UserRestView;
import ru.yandex.practicum.filmorate.service.varimpl.FilmService;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmServiceController {
    @Qualifier("filmService")
    private final FilmService filmService;
    private final FilmMapper filmMapper;
    private final UserMapper userMapper;

    @GetMapping("/popular")
    public List<FilmRestView> getPopularFilms(@RequestParam(name = "count", defaultValue = "10")
                                                 @Positive int size) {
        List<Film> popularFilms = filmService.getMostLikedFilms(size);
        log.debug(String.format("Запрошен список из %d наиболее популярных фильмов", size));
        return popularFilms.stream()
                .map(filmMapper::toRestView)
                .collect(Collectors.toList());
    }

    @PutMapping("{film_id}/like/{user_id}")
    public List<UserRestView> addLike(@PathVariable(value = "film_id") @Positive long filmId,
                                      @PathVariable(value = "user_id") @Positive long userId) {
        LikeCommand likeCommand = new LikeCommand(filmId, userId);
        List<User> userList = filmService.addLikeToFilmLikesSet(likeCommand);
        log.debug(String.format("Пользователь id%d поставил лайк фильму id%d", userId, filmId));
        return userList.stream()
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @DeleteMapping("{film_id}/like/{user_id}")
    public List<UserRestView> removeLike(@PathVariable(value = "film_id") @Positive long filmId,
                                         @PathVariable(value = "user_id") @Positive long userId) {
        LikeCommand likeCommand = new LikeCommand(filmId, userId);
        List<User> userList = filmService.removeLikeFromFilmLikesSet(likeCommand);
        log.debug(String.format("Пользователь id%d убрал лайк с фильма id%d", userId, filmId));
        return userList.stream()
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @GetMapping("{film_id}/likes")
    List<UserRestView> getAllUsersWhoLikedFilm(@PathVariable(value = "film_id") @Positive long filmId) {
        List<User> userList = filmService.getAllUsersWhoLikedFilm(filmId);
        log.debug(String.format("Запрошен список всех пользователей, поставившиз лайк фильму с id%d", filmId));
        return userList.stream()
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @GetMapping("/likedfilms/{user_id}")
    List<FilmRestView> getAllFilmsLikedByUser(@PathVariable(value = "user_id") @Positive long userId) {
        List<Film> filmList = filmService.getAllFilmsLikedByUser(userId);
        log.debug(String.format("Запрошен список фильмов, которые лайкнул пользователь с id%d", userId));
        return filmList.stream()
                .map(filmMapper::toRestView)
                .collect(Collectors.toList());
    }

    @PutMapping("{film_id}/genre/{genre_id}")
    List<GenreRestView> addFilmGenreAssociation(@PathVariable(value = "film_id") @Positive long filmId,
                                                @PathVariable(value = "genre_id") @Positive long genreId) {
        Genre genre = Genre.getGenreById((int) genreId);
        List<Genre> genreList = filmService.addFilmGenreAssociation(
                new FilmGenreCommand(filmId, genre));
        log.debug(String.format("Фильму с id%d присвоен жанр '%s'", filmId, genre.getByRus()));
        return genreList.stream()
                .map(everyGenre -> new GenreRestView(everyGenre.getId(), everyGenre.getByRus()))
                .collect(Collectors.toList());
    }

    @DeleteMapping("{film_id}/genre/{genre_id}")
    List<GenreRestView> removeFilmGenreAssociation(@PathVariable(value = "film_id") @Positive long filmId,
                                           @PathVariable(value = "genre_id") @Positive long genreId) {
        Genre genre = Genre.getGenreById((int) genreId);
        List<Genre> genreList = filmService.removeFilmGenreAssociation(
                new FilmGenreCommand(filmId, genre));
        log.debug(String.format("Фильм с id%d больше не относится к жанру '%s'", filmId, genre.getByRus()));
        return genreList.stream()
                .map(everyGenre -> new GenreRestView(everyGenre.getId(), everyGenre.getByRus()))
                .collect(Collectors.toList());
    }

    @GetMapping("/bygenre/{genre_id}")
    List<FilmRestView> getAllFilmsByGenre(@PathVariable(value = "genre_id") @Positive long genreId) {
        Genre genre = Genre.getGenreById((int) genreId);
        List<Film> filmsByGenre = filmService.getAllFilmsByGenre(genre);
        log.debug(String.format("Запрошены все фильмы, относящиеся к жанру '%s'", genre.getByRus()));
        return filmsByGenre.stream()
                .map(filmMapper::toRestView)
                .collect(Collectors.toList());
    }

    @GetMapping("{film_id}/genres")
    List<GenreRestView> getGenresByFilmId(@PathVariable(value = "film_id") @Positive long filmId) {
        List<Genre> genreList = filmService.getFilmGenresByFilmId(filmId);
        log.debug(String.format("Запрошены все жанры фильма с идентификатором %d", filmId));
        return genreList.stream()
                .map(everyGenre -> new GenreRestView(everyGenre.getId(), everyGenre.getByRus()))
                .collect(Collectors.toList());
    }

    @GetMapping("/byrating/{rating_id}")
    List<FilmRestView> getAllFilmsByRatingMpa(@PathVariable(value = "rating_id") @Positive long ratingId) {
        RatingMpa ratingMpa = RatingMpa.getRatingById((int) ratingId);
        List<Film> filmsByRating = filmService.getAllFilmsByRatingMpa(ratingMpa);
        log.debug(String.format("Запрошены все фильмы, относящиеся к рейтингу '%s'", ratingMpa.getName()));
        return filmsByRating.stream()
                .map(filmMapper::toRestView)
                .collect(Collectors.toList());
    }

}