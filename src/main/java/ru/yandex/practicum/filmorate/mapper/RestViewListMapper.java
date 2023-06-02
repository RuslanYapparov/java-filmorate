package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.*;
import ru.yandex.practicum.filmorate.model.service.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RestViewListMapper {

    @Mapping(target = "mpa", source = "rating")
    @Mapping(target = "genres", source = "genres",qualifiedByName = "mapGenreSetRestView")
    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikes")
    @Mapping(target = "directors", source = "directors", qualifiedByName = "mapDirectorSetRestView")
    FilmRestView toRestView(Film film);

    @Mapping(target = "mpa", source = "rating")
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

    default RatingMpaRestView mapMpaRestView(RatingMpa rating) {
        return new RatingMpaRestView(rating.getId(), rating.getName());
    }

    @Named("mapGenreSetRestView")
    default Set<GenreRestView> mapGenreSetRestView(Set<Genre> genreSet) {
        Set<GenreRestView> filmGenres = new TreeSet<>(Comparator.comparingInt(GenreRestView::getId));
        if (genreSet != null) {
            filmGenres.addAll(genreSet.stream()
                    .map(genre -> new GenreRestView(genre.getId(), genre.getByRus()))
                    .collect(Collectors.toSet()));
            return filmGenres;
        }
        return new HashSet<>();
    }

    @Named("mapDirectorSetRestView")
    default Set<DirectorRestView> mapDirectorSetRestView(Set<Director> directorSet) {
        Set<DirectorRestView> filmDirectors = new TreeSet<>(Comparator.comparingInt(DirectorRestView::getId));
        if (directorSet != null) {
            filmDirectors.addAll(directorSet.stream()
                    .map(director -> new DirectorRestView(director.getId(), director.getName()))
                    .collect(Collectors.toSet()));
            return filmDirectors;
        }
        return new HashSet<>();
    }

    @Named("mapLikes")
    default Set<Long> mapLikesSet(Set<Long> likesSet) {
        if (likesSet != null) {
            return new TreeSet<>(likesSet);
        }
        return new HashSet<>();
    }

}