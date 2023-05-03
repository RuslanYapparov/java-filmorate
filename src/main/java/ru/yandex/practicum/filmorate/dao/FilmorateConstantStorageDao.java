package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FilmorateConstantStorageDao<E> { // Здесь и далее под E подразумевается Entity - тип сущности ДБ

    int getQuantity();      // На данном этапе метод не задействуется и вызывает дополнительные издержки в виде
                            // Необходимости переопределения для сущностей соединительных таблиц БД, но, кажется, в нем
                     // Есть смысл, например, для проверки соответстия количества константых сущностей (рейтинг, жанр)
    E getById(long id);

    List<E> getAll();

}