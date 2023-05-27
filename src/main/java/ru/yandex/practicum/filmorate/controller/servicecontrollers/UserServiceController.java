package ru.yandex.practicum.filmorate.controller.servicecontrollers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.presentation.restview.FilmRestView;
import ru.yandex.practicum.filmorate.model.service.Film;
import ru.yandex.practicum.filmorate.model.service.FriendshipRequest;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.presentation.restview.UserRestView;
import ru.yandex.practicum.filmorate.service.varimpl.FilmService;
import ru.yandex.practicum.filmorate.service.varimpl.UserService;

@Validated
@RestController
@RequestMapping("/users/{user_id}")
@Slf4j
@RequiredArgsConstructor
public class UserServiceController {
    @Qualifier("userService")
    private final UserService userService;
    @Qualifier("filmService")
    private final FilmService filmService;
    private final UserMapper userMapper;
    private final FilmMapper filmMapper;

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
        FriendshipRequest friendshipRequest = new FriendshipRequest(userId, friendId);
        List<User> usersFriendsList = userService.addUserToAnotherUserFriendsSet(friendshipRequest);
        log.debug(String.format("Пользователи id%d id%d теперь друзья", userId, friendId));
        return this.mapListOfUsersToListOfUserRestViews(usersFriendsList);
    }

    @DeleteMapping("friends/{friend_id}")
    public List<UserRestView> removeFromFriendsSet(@PathVariable(value = "user_id") @Positive long userId,
                                                   @PathVariable(value = "friend_id") @Positive long friendId) {
        FriendshipRequest friendshipRequest = new FriendshipRequest(userId, friendId);
        List<User> usersFriendsList = userService.removeUserFromAnotherUserFriendsSet(friendshipRequest);
        log.debug(String.format("Пользователи id%d id%d больше не друзья", userId, friendId));
        return this.mapListOfUsersToListOfUserRestViews(usersFriendsList);
    }

    @GetMapping("/recommendations")
    public List<FilmRestView> getRecommendedFilmsForUser(@PathVariable(value = "user_id") @Positive long userId) {
        List<Film> recommendedFilms = filmService.getRecommendedFilmsForUser(userId);
        log.debug(String.format("Запрошен список рекоммендованных фильмов для пользователя id%d", userId));
        return this.mapListOfFilmsToListOfFilmRestViews(recommendedFilms);
    }

    private List<UserRestView> mapListOfUsersToListOfUserRestViews(List<User> users) {
        return users.stream()
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    private List<FilmRestView> mapListOfFilmsToListOfFilmRestViews(List<Film> films) {
        return films.stream()
                .map(filmMapper::toRestView)
                .collect(Collectors.toList());
    }

}