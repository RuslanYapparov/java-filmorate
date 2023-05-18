package ru.yandex.practicum.filmorate.dao.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ru.yandex.practicum.filmorate.dao.varimpl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.dao.varimpl.FriendshipDao;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.FriendshipEntity;
import ru.yandex.practicum.filmorate.model.service.FriendshipRequest;

/* Здесь несоответствие ТЗ и тестов в Postman (проверяется, что если пользователь отправил запрос, то у него в Set
 * с друзьями должна появиться запись о новом друге, а у получателя до одобрения запроса его нет). В любом случае,
 * чтобы поменять логику, нужно будет лишь исправить некоторые sql-запросы и последнюю строку операции flatMap в методе
 * initializeTransducer() на return Stream.of(new FriendshipRequest(fse.getUserId(), fse.getUserId())); */

@Repository
@Qualifier("friendshipRepository")
public class FriendshipDaoImpl extends FilmorateVariableStorageDaoImpl<FriendshipEntity, FriendshipRequest>
        implements FriendshipDao {
    Function<List<FriendshipEntity>, List<FriendshipRequest>> friendshipListTransducer;

    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.type = "friendship";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                new FriendshipEntity(resultSet.getLong("user_id"),
                        resultSet.getLong("friend_id"),
                        resultSet.getBoolean("confirmed"));
        // Чтобы не перегружать конструктор вывел инициализацию преобразователя с логикой дружбы в отдельный метод
        this.friendshipListTransducer = initializeTransducer();
    }

    @Override
    public int getQuantity() {
        sql = String.format("select count(user_id) from %ss", type);
        return jdbcTemplate.queryForObject(sql, Integer.class, type);
    }

    @Override
    public FriendshipEntity getById(long id) {
        return null;
    }

    @Override
    public List<FriendshipEntity> getAll() {
        sql = "select * from friendships order by user_id";
        return jdbcTemplate.query(sql, objectEntityRowMapper);
    }

    @Override
    public FriendshipEntity deleteById(long userId, long friendId) throws ObjectNotFoundInStorageException {
        SqlRowSet friendshipRows;
        sql = "select * from friendships where (user_id = ? and friend_id = ?) or (user_id = ? and friend_id = ?)";

        try {
            friendshipRows = jdbcTemplate.queryForRowSet(sql, userId, friendId, friendId, userId);
            if (!friendshipRows.next()) {
                throw new DataRetrievalFailureException("");
            }
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Данные не могут быть удалены, " +
            "т.к. запрос на дружбу пользователей с идентификаторами id%d и id%d не был сохранен", userId, friendId));
        }

// Запись о дружбе 2х пользователей в БД только одна. Может поменяться статус подтверждения или порядок пользователей
        FriendshipEntity friendshipEntity = jdbcTemplate.queryForObject(sql, objectEntityRowMapper,
                userId, friendId, friendId, userId);
        // Логика удаления из списка друзей: проверили наличие записи о дружбе и после проверки смотрим на ее статус
            if (friendshipEntity.isConfirmed()) { // Если дружба подтвержденная, то нужно, чтобы
// Осталась запись о том, что бывший друг все еще хочет дружить (подписчик), а пользователю вернется запрос на дружбу
                sql = "update friendships set user_id = ? friend_id = ? confirmed = false " +
                            "where (user_id = ? and friend_id = ?) or (user_id = ? and friend_id = ?)";
                jdbcTemplate.update(sql, friendId, userId, userId, friendId, friendId, userId);
            } else {             // Если запрос не подтвержден, то просто удаляем запись о нем из таблицы базы данных
                sql = "delete from friendships where user_id = ? and friend_id = ?";
                jdbcTemplate.update(sql, userId, friendId);
            }
        return friendshipEntity;
    }

    @Override
    public FriendshipEntity save(FriendshipRequest friendshipRequest) {
        long userId = friendshipRequest.getUserId();
        long friendId = friendshipRequest.getFriendId();
        sql = "select * from friendships where user_id = ? and friend_id = ?";       // Логика добавления в друзья:
        try {                                       // Сначала проверяем не повторный ли это запрос от пользователя
            SqlRowSet friendshipRows = jdbcTemplate.queryForRowSet(sql, userId, friendId);
            if (friendshipRows.next()) {
                throw new ObjectAlreadyExistsException("Запрос на дружбу уже был отправлен ранее");
            }                          // Потом проверяем это запрос на добавление или подтверждение чьего-то запроса
            friendshipRows = jdbcTemplate.queryForRowSet(sql, friendId, userId);
            if (friendshipRows.next()) {     // Если запись о запросе имеется, значит пользователь подтверждает дружбу
                return update(friendshipRequest);                          // И нужно просто обновить запись на true
            } else {                         // Если записи нет, то создаем новую неподтвержденную запись в таблице
                return createFriendshipRow(userId, friendId);
            }
        } catch (DataRetrievalFailureException exception) {
            return createFriendshipRow(userId, friendId);
        }
    }

    @Override
    public FriendshipEntity update(FriendshipRequest friendshipRequest) {
        sql = "update friendships set confirmed = true where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, friendshipRequest.getFriendId(), friendshipRequest.getUserId());
        sql = "select * from friendships user_id = ? and friend_id = ?";
        return jdbcTemplate.queryForObject(sql, objectEntityRowMapper,
                friendshipRequest.getFriendId(),
                friendshipRequest.getUserId());
    }

    @Override
    public List<FriendshipRequest> getAllByUserId(long userId) throws ObjectNotFoundInStorageException {
        sql = "select * from friendships where user_id = ? or (friend_id = ? and confirmed = true)";
        try {
            List<FriendshipEntity> friendshipEntities = jdbcTemplate.query(sql, objectEntityRowMapper, userId, userId);
            return friendshipListTransducer.apply(friendshipEntities);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format(
                    "У пользователя с id%d пока нет друзей...", userId));
        }
    }

    @Override
    public List<FriendshipRequest> getAllByTwoUserId(long userId, long friendId)
            throws ObjectNotFoundInStorageException {
        sql = "select * from friendships where user_id = ? or (friend_id = ? and confirmed = true) union " +
                "select * from friendships where user_id = ? or (friend_id = ? and confirmed = true)";
        try {
            List<FriendshipEntity> friendshipEntities = jdbcTemplate.query(sql, objectEntityRowMapper,
                    userId, userId, friendId, friendId);
            return friendshipListTransducer.apply(friendshipEntities);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException("У одного из пользователей пока нет друзей...");
        }
    }

    @Override
    public List<FriendshipRequest> getAllFriendshipRequests() {
        return friendshipListTransducer.apply(this.getAll());
    }

    private FriendshipEntity createFriendshipRow(long userId, long friendId) {
        sql = "insert into friendships (user_id, friend_id, confirmed) values (?, ?, false)";
        jdbcTemplate.update(sql, userId, friendId);
        sql = "select * from friendships where user_id = ? and friend_id = ?";
        return jdbcTemplate.queryForObject(sql, objectEntityRowMapper, userId, friendId);
    }

    private Function<List<FriendshipEntity>, List<FriendshipRequest>> initializeTransducer() {
        return fseList -> fseList.stream()
                .flatMap(fse -> {                               // Логика отсева объектов Friendship (далее - дружба):
                    if (fse.isConfirmed()) {     // В потоке останутся все подтвержденные дружбы, в которых фигурирует
                        return Stream.of(new FriendshipRequest(fse.getUserId(), fse.getFriendId()),    // Пользователь
                                         new FriendshipRequest(fse.getFriendId(), fse.getUserId()));
                    } else {                   // И неподтвержденные дружбы, в которых пользователь добавляет в друзья
                        return Stream.of(new FriendshipRequest(fse.getUserId(), fse.getFriendId()));
                    }
                })
                .collect(Collectors.toList());
    }

}