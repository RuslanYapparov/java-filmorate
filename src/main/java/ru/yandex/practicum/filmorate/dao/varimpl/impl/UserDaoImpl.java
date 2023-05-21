package ru.yandex.practicum.filmorate.dao.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;

import ru.yandex.practicum.filmorate.customvalidation.customvalidators.UserEmailAndNameValidator;
import ru.yandex.practicum.filmorate.dao.varimpl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.EmailValidationException;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.data.UserEntity;

@Repository
@Qualifier("userRepository")
public class UserDaoImpl extends FilmorateVariableStorageDaoImpl<UserEntity, User> {

    public UserDaoImpl(JdbcTemplate template) {
        super(template);
        this.type = "user";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                UserEntity.builder()
                        .id(resultSet.getLong("user_id"))
                        .email(resultSet.getString("email"))
                        .login(resultSet.getString("login"))
                        .name(resultSet.getString("user_name"))
                        .birthday(resultSet.getDate("birthday").toLocalDate())
                        .build();
    }

    @Override
    public UserEntity save(User user) throws EmailValidationException {
        //  Для сохранения поступает новый пользователь, поэтому его список друзей точно пуст
        KeyHolder keyHolder = new GeneratedKeyHolder();

        sql = "select email from users";
        List<String> emails = jdbcTemplate.queryForList(sql, String.class);
        user = UserEmailAndNameValidator.checkUserBeforeSaving(user, emails);
        String email = user.getEmail();
        String login = user.getLogin();
        String name = user.getName();
        Date birthday = Date.valueOf(user.getBirthday());
        sql = "insert into users (email, login, user_name, birthday) values (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, login);
            ps.setString(3, name);
            ps.setDate(4, birthday);
            return ps;
            }, keyHolder);
        return this.getById(keyHolder.getKey().longValue());
    }

    @Override
    public UserEntity update(User user) throws ObjectNotFoundInStorageException, EmailValidationException {
        user = UserEmailAndNameValidator.getUserWithCheckedName(user);
        sql = "update users set email = ?, login = ?, user_name = ?, birthday = ? " +
                "where user_id = ?";
        try {
            jdbcTemplate.update(sql,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    Date.valueOf(user.getBirthday()),
                    user.getId());
            return this.getById(user.getId());
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException("Данные не могут быть обновлены, т.к. пользователь " +
                    "с указанным идентификатором не был сохранен");
        }
    }

}