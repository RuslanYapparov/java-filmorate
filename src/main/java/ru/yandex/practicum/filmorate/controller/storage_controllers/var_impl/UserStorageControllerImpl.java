package ru.yandex.practicum.filmorate.controller.storage_controllers.var_impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.controller.storage_controllers.VariableStorageController;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.UserRestCommand;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.UserRestView;
import ru.yandex.practicum.filmorate.service.var_impl.UserService;

@Validated
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserStorageControllerImpl implements VariableStorageController<UserRestCommand, UserRestView> {
    @Qualifier("userService")
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    @GetMapping
    public List<UserRestView> getAll() {
        /* Логирую не только методы, меняющие состояние (как указано в задании), но и запросы данных,
         * наверное, это позволит отслеживать поведение пользователя, а в случае ошибки при получении по id -
         * узнать, когда и из-за чего произошла ошибка. В настройках программы установил уровень логирования DEBUG */
        log.debug("Запрошен список всех пользователей. Количество сохраненных пользователей: {}", userService.getQuantity());
        return userService.getAll().stream()
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("{user_id}")
    public UserRestView getOneById(@PathVariable(value = "user_id") @Positive long userId) {
        User user = userService.getById(userId);
        log.debug("Запрошен пользователь с идентификатором {}. Пользователь найден и отправлен клиенту", user.getId());
        return userMapper.toRestView(user);
    }

    @Override
    @PostMapping
    public UserRestView post(@RequestBody @Valid UserRestCommand postUserCommand) {
        User user = userService.save(postUserCommand);
        log.debug("Сохранен новый пользователь с логином '{}'. Присвоен идентификатор {}",
                user.getLogin(), user.getId());
        return userMapper.toRestView(user);
    }

    @Override
    @PutMapping
    public UserRestView put(@RequestBody UserRestCommand putUserCommand) {
        User user = userService.update(putUserCommand);
        log.debug("Обновлены данные пользователя с логином '{}'. Идентификатор пользователя: {}", user.getLogin(),
                user.getId());
        return userMapper.toRestView(user);
    }

    @Override
    @DeleteMapping
    public void deleteAll() {
        log.debug("Удалены данные всех пользователей из хранилища");
        userService.deleteAll();
    }

    @Override
    @DeleteMapping("{user_id}")
    public UserRestView deleteOneById(@PathVariable(value = "user_id") @Positive long userId) {
        User user = userService.deleteById(userId);
        log.debug("Запрошено удаление пользователя с идентификатором {}. Пользователь удален", userId);
        return userMapper.toRestView(user);
    }

}