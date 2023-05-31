package ru.yandex.practicum.filmorate.dao.const_impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.data.RatingMpaEntity;

@Repository
public class RatingMpaDaoImpl extends FilmorateConstantStorageDaoImpl<RatingMpaEntity> {

    public RatingMpaDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.type = "mpa_rating";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                RatingMpaEntity.builder()
                        .id(resultSet.getInt("rating_id"))
                        .name(resultSet.getString("rating_name"))
                        .description(resultSet.getString("rating_description"))
                        .build();
    }

}