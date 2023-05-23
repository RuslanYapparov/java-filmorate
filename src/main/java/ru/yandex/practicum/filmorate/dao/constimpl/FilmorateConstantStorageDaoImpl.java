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
        /*String lowerKeyWord = keyWord.toLowerCase();
        List<E> result = new ArrayList<>();
        switch (parameter) {
            case "director":
                sql = String.format("select f.* from %ss f "
                        + "join directors on f.director_id = directors.director_id "
                        + "where lower(directors.director_name) like '%s' order by %s_id", type, lowerKeyWord, type);

                result.addAll(jdbcTemplate.query(sql, objectEntityRowMapper));
                return result;
            case "title":

                sql = String.format("select * from %ss where lower(%s_name) like '%s' order by %s_id",
                        type, type, lowerKeyWord, type);

                result.addAll(jdbcTemplate.query(sql, objectEntityRowMapper));
                return result;
            case "title,director":
            case "director,title":
                sql = String.format("select %ss.* from %ss join directors on %ss.director_id = directors.director_id "
                        + "where lower(directors.director_name) like '%s' "
                        + "or lower(%ss.%s_name) like '%s' order by %s_id",
                        type, type, type, lowerKeyWord, type, type, lowerKeyWord, type);

                 result.addAll(jdbcTemplate.query(sql, objectEntityRowMapper));
                 return result;
            default:
                throw new BadRequestParameterException("Указан неверный параметр для поиска: " + parameter);
        }*/
       return null;
    }
}