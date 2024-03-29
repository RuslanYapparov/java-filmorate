package ru.yandex.practicum.filmorate.dao.var_impl.impl;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.custom_validation.custom_validators.UserEmailAndNameValidator;
import ru.yandex.practicum.filmorate.dao.var_impl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.exception.InternalLogicException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.EmailValidationException;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.data.UserEntity;

@Repository
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
        long userId = Optional.ofNullable(keyHolder.getKey())
                .orElseThrow(() -> new InternalLogicException("Произошла непредвиденная ошбика сохранения " +
                        "пользователя '" + login + "'. Пожалуйста, повторите попытку. Если ошибка повторится, " +
                        "пожалуйста, свяжитесь с разработчиками приложения"))
                .longValue();
        return this.getById(userId);
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