package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.model.data.FilmEntity;
import ru.yandex.practicum.filmorate.model.domain.Film;
import ru.yandex.practicum.filmorate.model.domain.Genre;
import ru.yandex.practicum.filmorate.model.domain.RatingMpa;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.GenreRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.RatingMpaRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restview.FilmRestView;
import ru.yandex.practicum.filmorate.model.presentation.restview.GenreRestView;
import ru.yandex.practicum.filmorate.model.presentation.restview.RatingMpaRestView;

@Mapper(componentModel = "spring")
public interface FilmMapper {

    @Mapping(target = "mpa", source = "rating", qualifiedByName = "mapMpaRestView")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenreSetRestView")
    FilmRestView toRestView(Film film);

    @Mapping(target = "rating", source = "mpa", qualifiedByName = "mapRating")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenreSet")
    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikes")
    Film fromRestCommand(FilmRestCommand filmRestCommand);

    FilmEntity toDbEntity(Film film);

    @Mapping(target = "likes", source = "id", qualifiedByName = "createLikesSet")
    @Mapping(target = "genres", source = "id", qualifiedByName = "createGenreSet")
    Film fromDbEntity(FilmEntity filmEntity);

    default Integer ratingMpaToInt(RatingMpa ratingMpa) {
        return ratingMpa.getId();
    }

    default RatingMpa intToRatingMpa(Integer id) {
        return RatingMpa.getRatingById(id);
    }

    @Named("mapMpaRestView")
    default RatingMpaRestView mapMpaRestView(RatingMpa rating) {
        return new RatingMpaRestView(rating.getId(), rating.getName());
    }

    @Named("mapGenreSetRestView")
    default Set<GenreRestView> mapGenreSetRestView(Set<Genre> genreSet) {
        return genreSet.stream()
                .map(genre -> new GenreRestView(genre.getId(), genre.getByRus()))
                .collect(Collectors.toSet());
    }

    @Named("mapRating")
    default RatingMpa mapRating(RatingMpaRestCommand rating) {
        if (rating != null) {
            return RatingMpa.getRatingById(rating.getId());
        }
        return RatingMpa.G;
    }

    @Named("mapGenreSet")
    default Set<Genre> mapGenreSet(Set<GenreRestCommand> genreSet) {
        if (genreSet != null) {
            return genreSet.stream()
                    .map(genreRestCommand -> Genre.getGenreById(genreRestCommand.getId()))
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    @Named("mapLikes")
    default Set<Long> mapLikesSet(Set<Long> likesSet) {
        if (likesSet != null) {
            return likesSet;
        }
        return new HashSet<>();
    }

    @Named("createGenreSet")
    default Set<Genre> createGenreSet(long id) {
        return new HashSet<>();
    }

    @Named("createLikesSet")
    default Set<Long> createLikesSet(long id) {
        return new HashSet<>();
    }

}