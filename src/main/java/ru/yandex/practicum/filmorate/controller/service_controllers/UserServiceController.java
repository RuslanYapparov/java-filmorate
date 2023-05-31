package ru.yandex.practicum.filmorate.controller.service_controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.EventRestView;
import ru.yandex.practicum.filmorate.model.service.FriendshipRequest;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.UserRestView;
import ru.yandex.practicum.filmorate.service.var_impl.UserService;

@Validated
@RestController
@RequestMapping("/users/{user_id}")
@Slf4j
@RequiredArgsConstructor
public class UserServiceController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    @GetMapping("/friends")
    public List<UserRestView> getFriends(@PathVariable(value = "user_id") @Positive long userId) {
        log.debug("Запрошен список друзей пользователя с id" + userId);
        return this.mapListOfUsersToListOfUserRestViews(userService.getUsersFriendsSet(userId));
    }

    @GetMapping("/friends/common/{friend_id}")
    public List<UserRestView> getCommonFriends(@PathVariable(value = "user_id") @Positive long userId,
                                               @PathVariable(value = "friend_id") @Positive long friendId) {
        List<User> commonFriendsList = userService.getCommonFriendsOfTwoUsers(userId, friendId);
        log.debug(String.format("Запрошен список общих друзей пользователей id%d id%d", userId, friendId));
        return this.mapListOfUsersToListOfUserRestViews(commonFriendsList);
    }

    @PutMapping("/friends/{friend_id}")
    public List<UserRestView> addToFriendsSet(@PathVariable(value = "user_id") @Positive long userId,
                                              @PathVariable(value = "friend_id") @Positive long friendId) {
        FriendshipRequest friendshipRequest = FriendshipRequest.builder().userId(userId).friendId(friendId).build();
        List<User> usersFriendsList = userService.addUserToAnotherUserFriendsSet(friendshipRequest);
        log.debug(String.format("Пользователи id%d id%d теперь друзья", userId, friendId));
        return this.mapListOfUsersToListOfUserRestViews(usersFriendsList);
    }

    @DeleteMapping("/friends/{friend_id}")
    public List<UserRestView> removeFromFriendsSet(@PathVariable(value = "user_id") @Positive long userId,
                                                   @PathVariable(value = "friend_id") @Positive long friendId) {
        FriendshipRequest friendshipRequest = FriendshipRequest.builder().userId(userId).friendId(friendId).build();
        List<User> usersFriendsList = userService.removeUserFromAnotherUserFriendsSet(friendshipRequest);
        log.debug(String.format("Пользователи id%d id%d больше не друзья", userId, friendId));
        return this.mapListOfUsersToListOfUserRestViews(usersFriendsList);
    }

    @GetMapping("/feed")
    public List<EventRestView> getUserFeed(@PathVariable(value = "user_id") @Positive long userId) {
        List<EventRestView> userFeed = userService.getAllEventsByUserId(userId).stream()
                .map(eventMapper::toRestView)
                .collect(Collectors.toList());
        log.debug(String.format("Запрошена лента событий пользователя с id%d", userId));
        return userFeed;
    }

    private List<UserRestView> mapListOfUsersToListOfUserRestViews(List<User> users) {
        return users.stream()
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

}