package ru.yandex.practicum.filmorate.controller.storagecontrollers.impl;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

@Validated
@RestController
@lombok.extern.slf4j.Slf4j
@RequestMapping("/users")
@lombok.RequiredArgsConstructor
public class UserStorageControllerImpl implements UserStorageController {
    private final InMemoryStorage<User> userData;

    @Override
    @GetMapping
    public List<UserRestCommand> getAll() {
        /* Логирую не только методы, меняющие состояние (как указано в задании), но и запросы данных,
         * наверное, это позволит отслеживать поведение пользователя, а в случае ошибки при получении по id -
         * узнать, когда и из-за чего произошла ошибка. В настройках программы установил уровень логирования DEBUG */
        log.debug("Запрошен список всех пользователей. Количество сохраненных объектов: {}", userData.getSize());
        return userData.getAll().stream()
                .map(UserRestCommand::new)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("{user_id}")
    public UserRestCommand getOneById(@PathVariable(value = "user_id") @Positive long id)
            throws ObjectNotFoundInStorageException {
        User user = userData.getById(id);
        log.debug("Запрошен пользователь с идентификатором {}. Пользователь найден и отправлен клиенту", user.getId());
        return new UserRestCommand(user);
    }

    @PostMapping
    public UserRestCommand post(@RequestBody @Valid UserRestCommand userRestCommand) throws UserValidationException {
        User user = userRestCommand.convertToDomainObject();
        user = userData.save(user);
        log.debug("Сохранен новый пользователь с логином '{}'. Присвоен идентификатор {}",
                user.getLogin(), user.getId());
        return new UserRestCommand(user);
    }

    @PutMapping
    public UserRestCommand put(@RequestBody UserRestCommand userRestCommand)
            throws ObjectNotFoundInStorageException {
        User user = userRestCommand.convertToDomainObject();
        user = userData.update(user);
        log.debug("Обновлены данные пользователя с логином '{}'. Идентификатор пользователя: {}", user.getLogin(),
                user.getId());
        return new UserRestCommand(user);
    }

    @Override
    @DeleteMapping
    public void deleteAll() {
        log.debug("Удалены данные всех пользователей из хранилища");
        userData.deleteAll();
    }

    @Override
    @DeleteMapping("{user_id}")
    public UserRestCommand deleteOneById(@PathVariable(value = "user_id") @Positive long id)
            throws ObjectNotFoundInStorageException {
        User user = userData.deleteById(id);
        log.debug("Запрошено удаление пользователя с идентификатором {}. Пользователь удален", id);
        return new UserRestCommand(user);
    }

}