package ru.yandex.practicum.filmorate.dao.constimpl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.data.GenreEntity;

@Repository
@Qualifier("genreRepository")
public class GenreDaoImpl extends FilmorateConstantStorageDaoImpl<GenreEntity> {

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.type = "genre";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                new GenreEntity(resultSet.getInt("genre_id"),
                        resultSet.getString("genre_name"));
    }

}