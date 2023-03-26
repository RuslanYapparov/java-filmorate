package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

@Service
public class UserStorage extends InMemoryStorageImpl<User> {

}