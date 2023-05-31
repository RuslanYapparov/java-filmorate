package ru.yandex.practicum.filmorate.dao.var_impl.impl;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.var_impl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.DirectorEntity;
import ru.yandex.practicum.filmorate.model.service.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class DirectorDaoImpl extends FilmorateVariableStorageDaoImpl<DirectorEntity, Director> {

    public DirectorDaoImpl(JdbcTemplate template) {
        super(template);
        this.type = "director";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                DirectorEntity.builder()
                        .id(resultSet.getInt("director_id"))
                        .name(resultSet.getString("director_name"))
                        .build();
    }

    @Override
    public DirectorEntity save(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String name = director.getName();

        sql = "insert into directors (director_name) values (?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            return ps;
            }, keyHolder);
        return this.getById(keyHolder.getKey().longValue());
    }

    @Override
    public DirectorEntity update(Director director) throws ObjectNotFoundInStorageException {
        int directorId = director.getId();
        sql = "update directors set director_name = ? where director_id = ?";
        try {
            jdbcTemplate.update(sql, director.getName(), directorId);
            return this.getById(directorId);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException("Данные не могут быть обновлены, т.к. режиссёр " +
                    "с указанным идентификатором не был сохранен");
        }
    }

}