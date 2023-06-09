package ru.yandex.practicum.filmorate.controller.service_controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import ru.yandex.practicum.filmorate.mapper.RestViewListMapper;
import ru.yandex.practicum.filmorate.model.data.command.FilmGenreCommand;
import ru.yandex.practicum.filmorate.model.data.command.LikeCommand;
import ru.yandex.practicum.filmorate.model.service.*;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.FilmRestView;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.GenreRestView;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.UserRestView;
import ru.yandex.practicum.filmorate.service.var_impl.FilmService;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmServiceController {
    private final FilmService filmService;
    private final RestViewListMapper restViewListMapper;

    @GetMapping("/popular")
    public List<FilmRestView> getPopularFilmsWithFilters(
            @RequestParam(name = "count", defaultValue = "10") @Positive int count,
            @RequestParam(name = "genreId", defaultValue = "7777") @Positive int genreId,
            @RequestParam(name = "year", defaultValue = "7777") @Positive @Min(1895) int year) {
        List<Film> popular = filmService.getMostLikedFilmsWithFilters(count, genreId, year);
        if (genreId == 7777 & year == 7777) {
            log.debug(String.format("Запрошен список из %d наиболее популярных фильмов", count));
        } else {
            log.debug(String.format("Запрошен список из %d наиболее популярных фильмов, отфильтрованных по %s%s",
                    count,
                    (genreId != 7777 ? "жанру  - " + Genre.getGenreById(genreId).getByRus() + " " : ""),
                    (year != 7777 ? "году выпуска - " + year : "")));
        }
        return restViewListMapper.mapListOfFilmsToListOfFilmRestViews(popular);
    }

    @GetMapping("/search")
    public List<FilmRestView> getPopularFilmsBySearch(@RequestParam(name = "query") @NotBlank String keyWord,
                                                      @RequestParam(name = "by", defaultValue = "title,director")
                                                      String parameter) {
        List<Film> searchFilms = filmService.getMostLikedFilmsBySearch(keyWord, parameter);
        log.debug(String.format("Запрошен поиск фильмов по ключевому слову '%s' в параметре '%s'. "
                + "Количество фильмов, удовлетворяющих условиям поиска: %d", keyWord, parameter, searchFilms.size()));
        return restViewListMapper.mapListOfFilmsToListOfFilmRestViews(searchFilms);
    }

    @PutMapping("{film_id}/like/{user_id}")
    public List<UserRestView> addLike(@PathVariable(value = "film_id") @Positive long filmId,
                                      @PathVariable(value = "user_id") @Positive long userId) {
        LikeCommand likeCommand = LikeCommand.builder().filmId(filmId).userId(userId).build();
        List<User> userList = filmService.addLikeToFilmLikesSet(likeCommand);
        log.debug(String.format("Пользователь id%d поставил лайк фильму id%d", userId, filmId));
        return restViewListMapper.mapListOfUsersToListOfUserRestViews(userList);
    }

    @DeleteMapping("{film_id}/like/{user_id}")
    public List<UserRestView> removeLike(@PathVariable(value = "film_id") @Positive long filmId,
                                         @PathVariable(value = "user_id") @Positive long userId) {
        LikeCommand likeCommand = LikeCommand.builder().filmId(filmId).userId(userId).build();
        List<User> userList = filmService.removeLikeFromFilmLikesSet(likeCommand);
        log.debug(String.format("Пользователь id%d убрал лайк с фильма id%d", userId, filmId));
        return restViewListMapper.mapListOfUsersToListOfUserRestViews(userList);
    }

    @GetMapping("{film_id}/likes")
    List<UserRestView> getAllUsersWhoLikedFilm(@PathVariable(value = "film_id") @Positive long filmId) {
        List<User> userList = filmService.getAllUsersWhoLikedFilm(filmId);
        log.debug(String.format("Запрошен список всех пользователей, поставившиз лайк фильму с id%d", filmId));
        return restViewListMapper.mapListOfUsersToListOfUserRestViews(userList);
    }

    @GetMapping("/likedfilms/{user_id}")
    List<FilmRestView> getAllFilmsLikedByUser(@PathVariable(value = "user_id") @Positive long userId) {
        List<Film> filmList = filmService.getAllFilmsLikedByUser(userId);
        log.debug(String.format("Запрошен список фильмов, которые лайкнул пользователь с id%d", userId));
        return restViewListMapper.mapListOfFilmsToListOfFilmRestViews(filmList);
    }

    @PutMapping("{film_id}/genre/{genre_id}")
    List<GenreRestView> addFilmGenreAssociation(@PathVariable(value = "film_id") @Positive long filmId,
                                                @PathVariable(value = "genre_id") @Positive int genreId) {
        List<Genre> genreList = filmService.addFilmGenreAssociation(
                FilmGenreCommand.builder().filmId(filmId).genreId(genreId).build());
        log.debug(String.format("Фильму с id%d присвоен жанр '%s'", filmId, Genre.getGenreById(genreId).getByRus()));
        return restViewListMapper.mapListOfGenresToListOfGenreRestViews(genreList);
    }

    @DeleteMapping("{film_id}/genre/{genre_id}")
    List<GenreRestView> removeFilmGenreAssociation(@PathVariable(value = "film_id") @Positive long filmId,
                                                   @PathVariable(value = "genre_id") @Positive int genreId) {
        List<Genre> genreList = filmService.addFilmGenreAssociation(
                FilmGenreCommand.builder().filmId(filmId).genreId(genreId).build());
        log.debug(String.format("Фильм с id%d больше не относится к жанру '%s'", filmId,
                Genre.getGenreById(genreId).getByRus()));
        return restViewListMapper.mapListOfGenresToListOfGenreRestViews(genreList);
    }

    @GetMapping("/bygenre/{genre_id}")
    List<FilmRestView> getAllFilmsByGenre(@PathVariable(value = "genre_id") @Positive long genreId) {
        Genre genre = Genre.getGenreById((int) genreId);
        List<Film> filmsByGenre = filmService.getAllFilmsByGenre(genre);
        log.debug(String.format("Запрошены все фильмы, относящиеся к жанру '%s'", genre.getByRus()));
        return restViewListMapper.mapListOfFilmsToListOfFilmRestViews(filmsByGenre);
    }

    @GetMapping("{film_id}/genres")
    List<GenreRestView> getGenresByFilmId(@PathVariable(value = "film_id") @Positive long filmId) {
        List<Genre> genreList = filmService.getFilmGenresByFilmId(filmId);
        log.debug(String.format("Запрошены все жанры фильма с идентификатором %d", filmId));
        return restViewListMapper.mapListOfGenresToListOfGenreRestViews(genreList);
    }

    @GetMapping("/byrating/{rating_id}")
    List<FilmRestView> getAllFilmsByRatingMpa(@PathVariable(value = "rating_id") @Positive long ratingId) {
        RatingMpa ratingMpa = RatingMpa.getRatingById((int) ratingId);
        List<Film> filmsByRating = filmService.getAllFilmsByRatingMpa(ratingMpa);
        log.debug(String.format("Запрошены все фильмы, относящиеся к рейтингу '%s'", ratingMpa.getName()));
        return restViewListMapper.mapListOfFilmsToListOfFilmRestViews(filmsByRating);
    }

    @GetMapping("/director/{director_id}")
    public List<FilmRestView> getFilmsByDirectorIdSortedByParameter(
            @PathVariable(value = "director_id") @Positive int id,
            @RequestParam(name = "sortBy") String param) {
        List<Film> filmsByDirector = filmService.getAllFilmsByDirectorIdSortedBySomeParameter(id, param);
        log.debug(String.format("Запрошен список фильмов режиссера с id%d с признаком сортировки %s", id, param));
        return restViewListMapper.mapListOfFilmsToListOfFilmRestViews(filmsByDirector);
    }

    @GetMapping("/common")
    public List<FilmRestView> getCommonFilmsOfTwoUsers(@RequestParam(name = "userId") long userId,
                                              @RequestParam(name = "friendId") long friendId) {
        List<Film> commonFilms = filmService.getCommonFilmsOfTwoUsers(userId, friendId);
        return restViewListMapper.mapListOfFilmsToListOfFilmRestViews(commonFilms);
    }

}