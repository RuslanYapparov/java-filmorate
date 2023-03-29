package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import ru.yandex.practicum.filmorate.exception.StorageManagementException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.customvalidation.customvalidators.UserEmailAndNameValidator;
import ru.yandex.practicum.filmorate.service.storage.InMemoryStorage;

@Validated   /* Аннотация для реализации валидации идентификаторов, указанных в пути запроса
* (для методов getUserBuId и deleteUserBuId)  */
@RestController
@RequestMapping("/users")
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class UserController {
    private final InMemoryStorage<User> userData;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        /* Логирую не только методы, меняющие состояние (как указано в задании), но и запросы данных,
        * наверное, это позволит отслеживать поведение пользователя, а в случае ошибки при получении по id -
        * узнать, когда и из-за чего произошла ошибка. В настройках программы установил уровень логирования DEBUG */
        log.debug("Запрошен список всех пользователей. " +
                        "Количество сохраненных пользователей: {}", userData.getSize());
        return ResponseEntity.ok(userData.getAll());
    }

    /* В задании не было указаний о реализации методов получения/удаления по id и удаления всех объектов, мне
    * это показалось странным и я решил расширить функционал данными методами.  */
    @GetMapping("{id}")
    public ResponseEntity<User> getUserById(@PathVariable(name = "id") @Positive int id)
            throws StorageManagementException {
        User user = userData.getById(id);
        log.debug("Запрошен пользователь с идентификатором {}. Пользователь найден и отправлен клиенту", id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> saveNewUser(@Valid @RequestBody User user) throws UserValidationException {
        user = UserEmailAndNameValidator.checkUserBeforeSaving(user, userData.getAll());
        int idForUser = userData.produceId();
        user = user.toBuilder().id(idForUser).build();
        userData.save(idForUser, user);
        log.debug("Сохранен новый пользователь с логином '{}'. Присвоен идентификатор {}", user.getLogin(), idForUser);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) throws StorageManagementException {
        userData.update(user.getId(), user);
        log.debug("Обновлены данные пользователя с логином '{}'. Идентификатор пользователя: {}", user.getLogin(),
                    user.getId());
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteAllUsers() {
        userData.deleteAll();
        log.debug("Удалены данные всех пользователей из хранилища");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable(name = "id") @Positive int id)
            throws StorageManagementException {
        User user = userData.deleteById(id);
        log.debug("Запрошено удаление пользователя с идентификатором {}. Пользователь удален", id);
        return ResponseEntity.ok(user);
    }

}