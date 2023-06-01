package ru.yandex.practicum.filmorate.dao.var_impl.impl;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

import ru.yandex.practicum.filmorate.dao.var_impl.*;
import ru.yandex.practicum.filmorate.exception.InternalLogicException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.FilmEntity;
import ru.yandex.practicum.filmorate.model.service.*;

@Repository
public class FilmDaoImpl extends FilmorateVariableStorageDaoImpl<FilmEntity, Film> {

    public FilmDaoImpl(JdbcTemplate template) {
        super(template);
        this.type = "film";
        this.objectEntityRowMapper = (resultSet, rowNumber) -> FilmEntity.builder()
                        .id(resultSet.getLong("film_id"))
                        .name(resultSet.getString("film_name"))
                        .description(resultSet.getString("film_description"))
                        .releaseDate(resultSet.getDate("release_date").toLocalDate())
                        .duration(resultSet.getInt("duration"))
                        .rate(resultSet.getByte("rate"))
                        .rating(resultSet.getInt("mpa_rating_id"))
                        .build();
    }

    @Override
    public FilmEntity save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String name = film.getName();
        String description = film.getDescription();
        Date releaseDate = Date.valueOf(film.getReleaseDate());
        int duration = film.getDuration();
        int rate = film.getRate();
        int rating = film.getRating().getId();
        sql = "insert into films (film_name, film_description, release_date, duration, rate, mpa_rating_id) " +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDate(3, releaseDate);
            ps.setInt(4, duration);
            ps.setInt(5, rate);
            ps.setInt(6, rating);
            return ps;
            }, keyHolder);
        long filmId = Optional.ofNullable(keyHolder.getKey())
                .orElseThrow(() -> new InternalLogicException("Произошла непредвиденная ошбика сохранения фильма '" +
                        name + "'. Пожалуйста, повторите попытку. Если ошибка повторится, пожалуйста, " +
                        "свяжитесь с разработчиками приложения"))
                .longValue();
        return this.getById(filmId);
    }

    @Override
    public FilmEntity update(Film film) throws ObjectNotFoundInStorageException {
        sql = "update films set film_name = ?, film_description = ?, release_date = ?, duration = ?, " +
                "rate = ?, mpa_rating_id = ? where film_id = ?";
        try {
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getRate(),
                    film.getRating().getId(),
                    film.getId());
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException("Данные не могут быть обновлены, т.к. фильм " +
                    "с указанным идентификатором не был сохранен");
        }
        return this.getById(film.getId());
    }
}