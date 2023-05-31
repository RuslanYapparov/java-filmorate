package ru.yandex.practicum.filmorate.dao.constimpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.data.GenreEntity;

@Repository
public class GenreDaoImpl extends FilmorateConstantStorageDaoImpl<GenreEntity> {

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.type = "genre";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                GenreEntity.builder()
                        .id(resultSet.getInt("genre_id"))
                        .name(resultSet.getString("genre_name"))
                        .build();
    }

}