package ru.yandex.practicum.filmorate.dao.constimpl;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import ru.yandex.practicum.filmorate.dao.FilmorateConstantStorageDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;

public class FilmorateConstantStorageDaoImpl<E> implements FilmorateConstantStorageDao<E> {
    protected String type;
    protected String sql;
    protected final JdbcTemplate jdbcTemplate;
    protected RowMapper<E> objectEntityRowMapper;

    public FilmorateConstantStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.type = "T";
    }

    @Override
    public int getQuantity() {
        sql = String.format("select count(%s_id) from %ss", type, type);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public E getById(long objectId) throws ObjectNotFoundInStorageException {
        sql = String.format("select * from %ss where %s_id = ?", type, type);
        try {
            return jdbcTemplate.queryForObject(sql, objectEntityRowMapper, objectId);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Объект %s с идентификатором %d отсутствует " +
                    "в базе данных приложения", type, objectId));
        }
    }

    @Override
    public List<E> getAll() {
        sql = String.format("select * from %ss order by %s_id", type, type);
        return jdbcTemplate.query(sql, objectEntityRowMapper);
    }

    @Override
    public List<E> getAllBySearch(String keyWord, String parameter) {
        if (parameter.equals("director")) {
            sql = String.format("select * from %ss join directors on %s.director_id = directors.id "
                    + "where lcase(directors.name) like lcase('%\\%s%') order by %s_id", type, type, keyWord, type);

            try {
                return jdbcTemplate.query(sql, objectEntityRowMapper, keyWord);
            } catch (DataRetrievalFailureException exception) {
                throw new ObjectNotFoundInStorageException(String.format("Объекты %s, имя режиссера которого "
                        + "содержит %s, отсутствуют в базе данных приложения", type, keyWord));
            }
        } else if (parameter.equals("title")) {
            sql = String.format("select * from %ss where lcase(%s_name) like lcase('%\\%s%') order by %s_id",
                    type, type, keyWord, type);
            try {
                return jdbcTemplate.query(sql, objectEntityRowMapper, keyWord);
            } catch (DataRetrievalFailureException exception) {
                throw new ObjectNotFoundInStorageException(String.format("Объекты %s, содержащие в названии %s, "
                        + "отсутствуют в базе данных приложения", type, keyWord));
            }
        }
        sql = String.format("select * from %ss join directors on %s.director_id = directors.id "
                + "where lcase(directors.name) like lcase('%" + keyWord
                + "%') and lcase(%s_name) like lcase('%" + keyWord + "%') order by %s_id", type, type, type, type);
        try {
            return jdbcTemplate.query(sql, objectEntityRowMapper, keyWord);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Объекты %s, название и имя режиссера которых "
                    + "содержит %s, отсутствуют в базе данных приложения", type, keyWord));
        }
    }
}