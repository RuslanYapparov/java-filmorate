package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import ru.yandex.practicum.filmorate.exception.StorageManagementException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.InMemoryStorage;
import ru.yandex.practicum.filmorate.service.InMemoryStorageImpl;
import ru.yandex.practicum.filmorate.customvalidation.customvalidators.UserValidator;

@RestController
@RequestMapping("/users")
@lombok.extern.slf4j.Slf4j
public class UserController {
    private final InMemoryStorage<User> userData;

    @Autowired      // Использую аннотацию с конструктором, а не полем, таким образом приложение начинает использовать
    private UserController() {           // Два экземпляра дженерик-класса, а не один (в случае аннотации над полем).
        this.userData = new InMemoryStorageImpl<>();  // Считается ли это нарушением шаблона проектирования Singleton?
    }

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
    * это показалось странным и я решил расширить функционал. Тем более гуглится за 15 секунд */
    @GetMapping("{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        /* Не стал оборачивать вызывающие исключения методы в отдельные try-catch, мне показалось, что в данном
        * случае это не сильно повлияет на работу метода и только ухудшит читаемость */
        try {
            int id = Integer.parseInt(userId.replace("id", ""));
            User user = userData.getById(id);
            log.debug("Запрошен пользователь с идентификатором {}. Пользователь найден и отправлен клиенту", id);
            return ResponseEntity.ok(user);
        } catch (StorageManagementException | NumberFormatException exception) {
            log.debug("Запрошен пользователь с идентификатором {}. Пользователь не найден", userId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<User> saveNewUser(@Valid @RequestBody User user) {
        /* Валидация пользователей реализована с помощью аннотаций (в том числе, собственных), но для полей email
        * и name есть особые условия проверки, для которых не придуммал способ реализации через аннотации.
        * Поэтому вывел валидацию в отдельный утилитарный класс UserValidator (в папке customvalidators)  */
        try {
            UserValidator.checkUsersEmailForDuplication(user, userData.getAll());
        } catch (ValidationException exception) {
            log.debug("Попытка сохранения пользователя не удалась. Причина: {}", exception.getMessage());
            return ResponseEntity.badRequest().body(user);
        }
        user = UserValidator.checkUserNameAndReturnValidUser(user);
        int idForUser = userData.produceId();
        user = user.toBuilder().id(idForUser).build();
        userData.save(idForUser, user);
        log.debug("Сохранен новый пользователь с логином '{}'. Присвоен идентификатор {}",
                    user.getLogin(), idForUser);
        return ResponseEntity.ok().body(user);        
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        try {
            user = UserValidator.checkUserNameAndReturnValidUser(user);
            userData.update(user.getId(), user);
            log.debug("Обновлены данные пользователя с логином '{}'. Идентификатор пользователя: {}", user.getLogin(),
                    user.getId());
            return ResponseEntity.ok().body(user);
        } catch (StorageManagementException exception) {
            log.debug("Попытка обновления данных пользователя не удалась. Причина: {}", exception.getMessage());
            return ResponseEntity.internalServerError().body(user);
        }
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteAllUsers() {
        userData.deleteAll();
        log.debug("Удалены данные всех пользователей из хранилища");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<User> deleteUserById(@PathVariable String userId) {
        try {            // Аналогично с методом getById не стал обрабатывать каждую функцию с исключением отдельно
            int id = Integer.parseInt(userId.replace("id",""));
            User user = userData.deleteById(id);
            log.debug("Запрошено удаление пользователя с идентификатором {}. Пользователь удален", id);
            return ResponseEntity.ok(user);
        } catch (StorageManagementException | NumberFormatException exception) {
            log.debug("Запрошено удаление пользователя с идентификатором {}. Пользователь не найден", userId);
            return ResponseEntity.notFound().build();
        }
    }

}