package ru.yandex.practicum.filmorate.service.var_impl;

import java.util.List;

import ru.yandex.practicum.filmorate.model.data.command.FilmGenreCommand;
import ru.yandex.practicum.filmorate.model.data.command.MarkCommand;
import ru.yandex.practicum.filmorate.model.service.*;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.FilmRestCommand;
import ru.yandex.practicum.filmorate.service.CrudService;

public interface FilmService extends CrudService<Film, FilmRestCommand> {

    List<User> addMarkToFilm(MarkCommand like);

    List<User> removeMarkFromFilm(MarkCommand like);

    List<Film> getMostLikedFilmsWithFilters(int count, int genreId, int year);

    List<Film> getMostLikedFilmsBySearch(String keyWord, String parameter);

    List<User> getAllUsersWhoRatedFilm(long filmId);

    List<Film> getAllFilmsRatedByUser(long userId);

    List<Genre> addFilmGenreAssociation(FilmGenreCommand filmGenre);

    List<Genre> removeFilmGenreAssociation(FilmGenreCommand filmGenre);

    List<Film> getAllFilmsByGenre(Genre genre);

    List<Genre> getFilmGenresByFilmId(long filmId);

    List<Film> getAllFilmsByRatingMpa(RatingMpa ratingMpa);

    List<Film> getAllFilmsByDirectorIdSortedBySomeParameter(int id, String sortParameter);

    List<Film> getRecommendedFilmsForUser(long userId);

    List<Film> getCommonFilmsOfTwoUsers(long userId, long friendId);

}