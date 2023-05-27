package ru.yandex.practicum.filmorate.service.varimpl;

import java.util.List;

import ru.yandex.practicum.filmorate.model.data.command.FilmGenreCommand;
import ru.yandex.practicum.filmorate.model.data.command.LikeCommand;
import ru.yandex.practicum.filmorate.model.service.*;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.FilmRestCommand;
import ru.yandex.practicum.filmorate.service.CrudService;

public interface FilmService extends CrudService<Film, FilmRestCommand> {

    List<User> addLikeToFilmLikesSet(LikeCommand like);

    List<User> removeLikeFromFilmLikesSet(LikeCommand like);

    List<Film> getMostLikedFilms(int count);

    List<Film> getMostLikedFilmsBySearch(String keyWord, String parameter);

    List<User> getAllUsersWhoLikedFilm(long filmId);

    List<Film> getAllFilmsLikedByUser(long userId);

    List<Genre> addFilmGenreAssociation(FilmGenreCommand filmGenre);

    List<Genre> removeFilmGenreAssociation(FilmGenreCommand filmGenre);

    List<Film> getAllFilmsByGenre(Genre genre);

    List<Genre> getFilmGenresByFilmId(long filmId);

    List<Film> getAllFilmsByRatingMpa(RatingMpa ratingMpa);

    List<Film> getAllFilmsByDirectorIdSortedBySomeParameter(int id, String sortParameter);

    List<Film> getRecommendedFilmsForUser(long userId);

}