package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.*;
import ru.yandex.practicum.filmorate.model.service.*;

import java.time.Instant;
import java.util.*;

@Mapper(componentModel = "spring")
public interface RestViewListMapper {

    List<FilmRestView> mapListOfFilmsToListOfFilmRestViews(List<Film> films);

    List<UserRestView> mapListOfUsersToListOfUserRestViews(List<User> users);

    List<GenreRestView> mapListOfGenresToListOfGenreRestViews(List<Genre> genres);

    List<EventRestView> mapListOfEventsToListOfEventRestViews(List<Event> events);

    List<ReviewRestView> mapListOfReviewsToListOfReviewRestViews(List<Review> events);

    default GenreRestView mapGenreRestView(Genre genre) {
        return new GenreRestView(genre.getId(), genre.getByRus());
    }

    default long mapInstant(Instant timestamp) {
        return timestamp.toEpochMilli();
    }

}
