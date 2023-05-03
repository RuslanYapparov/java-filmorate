package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;

import ru.yandex.practicum.filmorate.model.data.UserEntity;
import ru.yandex.practicum.filmorate.model.domain.User;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.UserRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restview.UserRestView;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserRestView toRestView(User user);

    User fromRestCommand(UserRestCommand userRestCommand);

    UserEntity toDbEntity(User user);

    @Mapping(target = "friends", source = "id", qualifiedByName = "createFriendsSet")
    User fromDbEntity(UserEntity userEntity);

    @Named("createFriendsSet")
    default Set<Long> createFriendsSet(long id) {
        return new HashSet<>();
    }

}