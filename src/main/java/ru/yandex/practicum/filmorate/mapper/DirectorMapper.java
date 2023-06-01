package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.model.data.DirectorEntity;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.DirectorRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.DirectorRestView;
import ru.yandex.practicum.filmorate.model.service.Director;

@Mapper(componentModel = "spring")
public interface DirectorMapper {

    DirectorRestView toRestView(Director director);

    Director fromRestCommand(DirectorRestCommand directorRestCommand);

    Director fromDbEntity(DirectorEntity directorEntity);

}