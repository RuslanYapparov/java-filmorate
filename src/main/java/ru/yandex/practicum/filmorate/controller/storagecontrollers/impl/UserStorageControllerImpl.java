package ru.yandex.practicum.filmorate.controller.storagecontrollers.impl;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.model.restinteractionmodel.restcommand.UserRestCommand;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.UserRestView;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.util.UserObjectConverter;

@Validated
@RestController
@lombok.extern.slf4j.Slf4j
@RequestMapping("/users")
@lombok.RequiredArgsConstructor
public class UserStorageControllerImpl implements UserStorageController {
    private final InMemoryStorage<User> userData;

    @Override
    @GetMapping
    public List<UserRestView> getAll() {
        /* Логирую не только методы, меняющие состояние (как указано в задании), но и запросы данных,
         * наверное, это позволит отслеживать поведение пользователя, а в случае ошибки при получении по id -
         * узнать, когда и из-за чего произошла ошибка. В настройках программы установил уровень логирования DEBUG */
        log.debug("Запрошен список всех пользователей. Количество сохраненных пользователей: {}", userData.getSize());
        return userData.getAll().stream()
                .map(UserObjectConverter::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("{user_id}")
    public UserRestView getOneById(@PathVariable(value = "user_id") @Positive long id)
            throws ObjectNotFoundInStorageException {
        User user = userData.getById(id);
        log.debug("Запрошен пользователь с идентификатором {}. Пользователь найден и отправлен клиенту", user.getId());
        return UserObjectConverter.toRestView(user);
    }

    @PostMapping
    public UserRestView post(@RequestBody @Valid UserRestCommand postUserCommand) throws UserValidationException {
        User user = UserObjectConverter.toDomainObject(postUserCommand);
        user = userData.save(user);
        log.debug("Сохранен новый пользователь с логином '{}'. Присвоен идентификатор {}",
                user.getLogin(), user.getId());
        return UserObjectConverter.toRestView(user);
    }

    @PutMapping
    public UserRestView put(@RequestBody UserRestCommand putUserCommand)
            throws ObjectNotFoundInStorageException {
        User user = UserObjectConverter.toDomainObject(putUserCommand);
        user = userData.update(user);
        log.debug("Обновлены данные пользователя с логином '{}'. Идентификатор пользователя: {}", user.getLogin(),
                user.getId());
        return UserObjectConverter.toRestView(user);
    }

    @Override
    @DeleteMapping
    public void deleteAll() {
        log.debug("Удалены данные всех пользователей из хранилища");
        userData.deleteAll();
    }

    @Override
    @DeleteMapping("{user_id}")
    public UserRestView deleteOneById(@PathVariable(value = "user_id") @Positive long id)
            throws ObjectNotFoundInStorageException {
        User user = userData.deleteById(id);
        log.debug("Запрошено удаление пользователя с идентификатором {}. Пользователь удален", id);
        return UserObjectConverter.toRestView(user);
    }

}