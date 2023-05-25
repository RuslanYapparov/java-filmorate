package ru.yandex.practicum.filmorate.dao.varimpl;

public interface ReviewDao {
    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId);

    void deleteDislike(long reviewId, long userId);
}
