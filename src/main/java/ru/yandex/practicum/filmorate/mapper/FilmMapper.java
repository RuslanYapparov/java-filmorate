package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.model.data.FilmEntity;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.DirectorRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.DirectorRestView;
import ru.yandex.practicum.filmorate.model.service.Director;
import ru.yandex.practicum.filmorate.model.service.Film;
import ru.yandex.practicum.filmorate.model.service.Genre;
import ru.yandex.practicum.filmorate.model.service.RatingMpa;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.GenreRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.RatingMpaRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.FilmRestView;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.GenreRestView;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.RatingMpaRestView;

@Mapper(componentModel = "spring")
public interface FilmMapper {

    @Mapping(target = "mpa", source = "rating")
    @Mapping(target = "genres", source = "genres",qualifiedByName = "mapGenreSetRestView")
    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikes")
    @Mapping(target = "directors", source = "directors", qualifiedByName = "mapDirectorSetRestView")
    FilmRestView toRestView(Film film);

    @Mapping(target = "rating", source = "mpa")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenreSet")
    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikes")
    @Mapping(target = "directors", source = "directors", qualifiedByName = "mapDirectorSet")
    Film fromRestCommand(FilmRestCommand filmRestCommand);

    @Mapping(target = "likes", source = "id", qualifiedByName = "createLikesSet")
    @Mapping(target = "genres", source = "id", qualifiedByName = "createGenreSet")
    @Mapping(target = "directors", source = "id", qualifiedByName = "createDirectorSet")
    Film fromDbEntity(FilmEntity filmEntity);

    default Integer ratingMpaToInt(RatingMpa ratingMpa) {
        return ratingMpa.getId();
    }

    default RatingMpa intToRatingMpa(Integer id) {
        return RatingMpa.getRatingById(id);
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

    default RatingMpa mapRating(RatingMpaRestCommand rating) {
        if (rating != null) {
            return RatingMpa.getRatingById(rating.getId());
        }
        return RatingMpa.G;
    }

    @Named("mapLikes")
    default Set<Long> mapLikesSet(Set<Long> likesSet) {
        if (likesSet != null) {
            return new TreeSet<>(likesSet);
        }
        return new HashSet<>();
    }

    @Named("mapGenreSet")
    default Set<Genre> mapGenreSet(Set<GenreRestCommand> genreRestCommandSet) {
        if (genreRestCommandSet != null) {
            return genreRestCommandSet.stream()
                    .map(genreRestCommand -> Genre.getGenreById(genreRestCommand.getId()))
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    @Named("mapDirectorSet")
    default Set<Director> mapDirectorSet(Set<DirectorRestCommand> directorRestCommandSet) {
        if (directorRestCommandSet != null) {
            return directorRestCommandSet.stream()
                    .map(directorRestCommand ->
                            Director.builder().id(directorRestCommand.getId()).name("name").build())
                    .collect(Collectors.toSet());
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

    @Named("createDirectorSet")
    default Set<Director> createDirectorSet(long id) {
        return new HashSet<>();
    }

}