package ru.yandex.practicum.filmorate.dao.var_impl;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.dao.const_impl.FilmorateConstantStorageDaoImpl;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;

public class FilmorateVariableStorageDaoImpl<E, T> extends FilmorateConstantStorageDaoImpl<E>
        implements FilmorateVariableStorageDao<E, T> {

    public FilmorateVariableStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public void deleteAll() {
        sql = String.format("delete from %ss", type);
        jdbcTemplate.update(sql);
    }

    @Override
    public E deleteById(long objectId, long anotherObjectId) throws ObjectNotFoundInStorageException {
        E objectEntity;
        sql = String.format("select * from %ss where %s_id = ?", type, type); // Для записей user и film установлено
        try {      // Каскадное удаление в соединительных таблицах базы данных (запросы дружбы, лайки, фильмы-жанры)
            objectEntity = jdbcTemplate.queryForObject(sql, objectEntityRowMapper, objectId);
            sql = String.format("delete from %ss where %s_id = ?", type, type);
            jdbcTemplate.update(sql, objectId);
            return objectEntity;
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Объект %s с идентификатором %d не найден " +
                    "в базе данных приложения", type, objectId));
        }
    }

    @Override
    public E save(T object) {
        return null;
    }

    @Override
    public E update(T object) {
        return null;
    }

}