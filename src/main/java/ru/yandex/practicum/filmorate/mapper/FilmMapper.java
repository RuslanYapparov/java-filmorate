package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.model.dto.restcommand.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.dto.restview.FilmRestView;
import ru.yandex.practicum.filmorate.model.FilmModel;

@Mapper(componentModel = "spring")
public interface FilmMapper {

    FilmRestView toRestView(FilmModel userModel);

    FilmModel toModel(FilmRestCommand filmRestCommand);

}