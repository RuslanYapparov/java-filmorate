package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.filmorate.model.data.ReviewEntity;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.ReviewRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restview.ReviewRestView;
import ru.yandex.practicum.filmorate.model.service.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewRestView toRestView(Review review);

    Review fromRestCommand(ReviewRestCommand reviewRestCommand);

    Review fromDbEntity(ReviewEntity reviewEntity);

}