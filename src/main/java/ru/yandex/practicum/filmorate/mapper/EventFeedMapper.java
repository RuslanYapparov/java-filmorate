package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.model.data.EventFeedEntity;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.EventFeedRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restview.EventFeedRestView;
import ru.yandex.practicum.filmorate.model.service.EventFeed;

@Mapper(componentModel = "spring")
public interface EventFeedMapper {

    EventFeedRestView toRestView(EventFeed eventFeed);

    EventFeed fromDbEntity(EventFeedEntity eventFeedEntity);

    EventFeed fromRestCommand(EventFeedRestCommand eventFeedRestCommand);
}
