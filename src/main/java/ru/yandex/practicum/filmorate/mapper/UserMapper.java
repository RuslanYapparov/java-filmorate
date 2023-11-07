package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;

import ru.yandex.practicum.filmorate.model.data.UserEntity;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.UserRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.UserRestView;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "friends", source = "friends", qualifiedByName = "mapFriends")
    UserRestView toRestView(User user);

    @Mapping(target = "friends", source = "friends", qualifiedByName = "mapFriends")
    User fromRestCommand(UserRestCommand userRestCommand);

    @Mapping(target = "friends", expression = "java(new java.util.HashSet<>())")
    User fromDbEntity(UserEntity userEntity);

    @Named("mapFriends")
    default Set<Long> mapFriendsSet(Set<Long> friendsSet) {
        if (friendsSet != null) {
            return friendsSet;
        }
        return new HashSet<>();
    }

}