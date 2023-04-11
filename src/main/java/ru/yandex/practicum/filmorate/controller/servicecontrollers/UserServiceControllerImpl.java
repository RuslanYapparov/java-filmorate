package ru.yandex.practicum.filmorate.controller.servicecontrollers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Positive;

import java.util.List;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.UserRestView;
import ru.yandex.practicum.filmorate.service.UserService;

@Validated
@RestController
@lombok.extern.slf4j.Slf4j
@RequestMapping("/users/{user_id}/friends")
@lombok.RequiredArgsConstructor
public class UserServiceControllerImpl implements UserServiceController {
    private final UserService service;

    @Override
    @GetMapping
    public List<UserRestView> getFriends(@PathVariable(value = "user_id") @Positive long id)
            throws ObjectNotFoundInStorageException {
        log.debug("Запрошен список друзей пользователя с id" + id);
        return service.getUsersFriendsSet(id);
    }

    @Override
    @GetMapping("/common/{friend_id}")
    public List<UserRestView> getCommonFriends(@PathVariable(value = "user_id") @Positive long userId,
                                                  @PathVariable(value = "friend_id") @Positive long friendId)
        throws ObjectNotFoundInStorageException {
        List<UserRestView> commonFriendsList = service.getCommonFriendsOfTwoUsers(userId, friendId);
        log.debug(String.format("Запрошен список общих друзей пользователей id%d id%d", userId, friendId));
        return commonFriendsList;
    }

    @Override
    @DeleteMapping("{friend_id}")
    public List<UserRestView> removeFromFriendsSet(@PathVariable(value = "user_id") @Positive long userId,
                                          @PathVariable(value = "friend_id") @Positive long friendId)
        throws ObjectNotFoundInStorageException {
        List<UserRestView> usersFriendsList = service.removeUserFromAnotherUserFriendsSet(userId, friendId);
        log.debug(String.format("Пользователи id%d id%d больше не друзья", userId, friendId));
        return usersFriendsList;
    }

    @Override
    @PutMapping("{friend_id}")
    public List<UserRestView> addToFriendsSet(@PathVariable(value = "user_id") @Positive long userId,
                                     @PathVariable(value = "friend_id") @Positive long friendId)
            throws ObjectNotFoundInStorageException {
        List<UserRestView> usersFriendsList = service.addUserToAnotherUserFriendsSet(userId, friendId);
        log.debug(String.format("Пользователи id%d id%d теперь друзья", userId, friendId));
        return usersFriendsList;
    }

}