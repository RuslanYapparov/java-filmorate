package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FilmorateConstantStorageDao<E> { // Здесь и далее под E подразумевается Entity - тип сущности ДБ
    /* На данном этапе метод getQuantity не задействуется и вызывает дополнительные издержки в виде необходимости
    * переопределения для сущностей соединительных таблиц БД, но, кажется, в нем eсть смысл, например, для проверки
    * соответстия количества константых сущностей (рейтинг, жанр)     */
    int getQuantity();

    E getById(long id);

    List<E> getAll();

}