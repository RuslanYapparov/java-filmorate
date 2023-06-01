package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.model.data.EventEntity;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.EventRestView;
import ru.yandex.practicum.filmorate.model.service.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "timestamp", expression = "java(event.getTimestamp().toEpochMilli())")
    EventRestView toRestView(Event event);

    Event fromDbEntity(EventEntity eventEntity);

}
