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
    /* Не смог найти способ, чтобы в имплементации производилось действие при значении поля friends = null.
    * Применение парамтеров nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
    * nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS не помогло, в UserMapperImpl все равно обработка поля
    * выглядела так:
    *
    * Set<Long> set = user.getFriends();
    *    if ( set != null ) {
    *        userRestView.friends( new LinkedHashSet<Long>( set ) );
    *    }
    *
    * В результате получал NPE. Пока решил оставить такое дефолтное определение метода */
}