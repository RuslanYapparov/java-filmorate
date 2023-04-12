package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.model.dto.restcommand.UserRestCommand;
import ru.yandex.practicum.filmorate.model.dto.restview.UserRestView;
import ru.yandex.practicum.filmorate.model.UserModel;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserRestView toRestView(UserModel userModel);

    UserModel toModel(UserRestCommand userRestCommand);

}