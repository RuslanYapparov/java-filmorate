package ru.yandex.practicum.filmorate.controller.storagecontrollers;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.dto.restcommand.UserRestCommand;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.UserModel;
import ru.yandex.practicum.filmorate.model.dto.restview.UserRestView;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

@Validated
@RestController
@lombok.extern.slf4j.Slf4j
@RequestMapping("/users")
@lombok.RequiredArgsConstructor
public class UserStorageControllerImpl implements UserStorageController {
    private final InMemoryStorage<UserModel> userData;
    private final UserMapper userMapper;

    @Override
    @GetMapping
    public List<UserRestView> getAll() {
        /* Логирую не только методы, меняющие состояние (как указано в задании), но и запросы данных,
         * наверное, это позволит отслеживать поведение пользователя, а в случае ошибки при получении по id -
         * узнать, когда и из-за чего произошла ошибка. В настройках программы установил уровень логирования DEBUG */
        log.debug("Запрошен список всех пользователей. Количество сохраненных пользователей: {}", userData.getSize());
        return userData.getAll().stream()
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("{user_id}")
    public UserRestView getOneById(@PathVariable(value = "user_id") @Positive long userId)
            throws ObjectNotFoundInStorageException {
        UserModel userModel = userData.getById(userId);
        log.debug("Запрошен пользователь с идентификатором {}. Пользователь найден и отправлен клиенту", userModel.getId());
        return userMapper.toRestView(userModel);
    }

    @PostMapping
    public UserRestView post(@RequestBody @Valid UserRestCommand postUserCommand) throws UserValidationException {
        UserModel userModel = userMapper.toModel(postUserCommand);
        userModel = userData.save(userModel);
        log.debug("Сохранен новый пользователь с логином '{}'. Присвоен идентификатор {}",
                userModel.getLogin(), userModel.getId());
        return userMapper.toRestView(userModel);
    }

    @PutMapping
    public UserRestView put(@RequestBody UserRestCommand putUserCommand)
            throws ObjectNotFoundInStorageException {
        UserModel userModel = userMapper.toModel(putUserCommand);
        userModel = userData.update(userModel);
        log.debug("Обновлены данные пользователя с логином '{}'. Идентификатор пользователя: {}", userModel.getLogin(),
                userModel.getId());
        return userMapper.toRestView(userModel);
    }

    @Override
    @DeleteMapping
    public void deleteAll() {
        log.debug("Удалены данные всех пользователей из хранилища");
        userData.deleteAll();
    }

    @Override
    @DeleteMapping("{user_id}")
    public UserRestView deleteOneById(@PathVariable(value = "user_id") @Positive long userId)
            throws ObjectNotFoundInStorageException {
        UserModel userModel = userData.deleteById(userId);
        log.debug("Запрошено удаление пользователя с идентификатором {}. Пользователь удален", userId);
        return userMapper.toRestView(userModel);
    }

}