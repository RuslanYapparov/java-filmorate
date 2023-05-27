package ru.yandex.practicum.filmorate.dao.varimpl.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Repository
@Qualifier("recommendationsDaoIpl")
public class RecommendationsDaoIpl {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RecommendationsDaoIpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void userExists(long userId) {
        Integer exists = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM films WHERE id = ?",
                Integer.class,
                userId
        );

        if (exists == 0) throw new ObjectNotFoundInStorageException("Фильм с таким id не найден!");
    }

    public Integer numberLikes(long userId) {
        Integer numberLikes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE user_id = ?",
                Integer.class,
                userId
        );
        return numberLikes;
    }

    public Set<Long> getRandomFilm() {
        Set<Long> filmSet = new HashSet<>();
        long maxIdFilm = 0;
        Long maxFilmId = jdbcTemplate.queryForObject("SELECT MAX(film_id) FROM films", Long.class);


        Random rand = new Random();

        do {
            filmSet.add(rand.nextLong(maxIdFilm)); // случайный id фильма
        } while (filmSet.size() < 5);

        return filmSet;
    }

    public final static double  COEFFICIENT_OF_COINCIDENCE = 0.8;
    // на солько должны совпадать списки лайков пользователей, 80%

    // список из тех пользователей чьи интересы совпали на 80% и боле с userId
    public List<Long> getUsersMatchingInterests(long userId){
        List<Long> listId = jdbcTemplate.query("SELECT l.user_id " +
                "FROM likes AS l " +
                "LEFT OUTER JOIN (SELECT * FROM likes WHERE user_id=?) AS e ON e.film_id = l.film_id " +
                "WHERE e.user_id IS NOT NULL " +
                "GROUP BY l.user_id " +
                "HAVING COUNT(*) >= ? *(SELECT COUNT(*) FROM likes WHERE user_id=?);", new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong(1);
            }
        },userId,COEFFICIENT_OF_COINCIDENCE,userId);

        return listId;
    }

    // список из 5 пользователей с самым большим совпадением по лайкам
    public List<Long> getUsersIdCoincidencesInterests(long userId){
        List<Long> listId = jdbcTemplate.query("SELECT l.user_id " +
                "FROM likes AS l " +
                "LEFT OUTER JOIN (SELECT * FROM likes WHERE user_id = ?) AS e ON e.film_id = l.film_id " +
                "WHERE e.user_id IS NOT NULL " +
                "GROUP BY l.user_id " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT 5;", new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong(1);
            }
        },userId);

        return listId;
    }

    // ищем фильмы которые не оценивал userId чтобы не рекомендовать фильмы которые он уже оценил
    public List<Long> getFilmIdNonMatchingMovies(long userId, long anotherUserId){
        List<Long> listId = jdbcTemplate.query("SELECT DISTINCT(l.film_id) " +
                "FROM likes AS l " +
                "LEFT OUTER JOIN (SELECT * FROM likes WHERE user_id = ?) AS e ON e.film_id = l.film_id " +
                "WHERE e.user_id IS NULL AND l.user_id = ?; -- пользователь 2", new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong(1);
            }
        },userId,anotherUserId);

        return listId;
    }

}
