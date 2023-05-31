package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.model.data.ReviewEntity;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.ReviewRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.ReviewRestView;
import ru.yandex.practicum.filmorate.model.service.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewRestView toRestView(Review review);

    Review fromRestCommand(ReviewRestCommand reviewRestCommand);

    Review fromDbEntity(ReviewEntity reviewEntity);
}