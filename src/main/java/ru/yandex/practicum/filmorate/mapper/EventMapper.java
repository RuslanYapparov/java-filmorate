package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.filmorate.model.data.EventEntity;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.EventRestView;
import ru.yandex.practicum.filmorate.model.service.Event;
import ru.yandex.practicum.filmorate.model.service.EventOperation;
import ru.yandex.practicum.filmorate.model.service.EventType;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "mapTimestamp")
    @Mapping(target = "eventType", source = "eventType", qualifiedByName = "mapEventTypeToString")
    @Mapping(target = "operation", source = "operation", qualifiedByName = "mapEventOperationToString")
    EventRestView toRestView(Event event);

    @Mapping(target = "eventType", source = "eventType", qualifiedByName = "mapEventTypeFromString")
    @Mapping(target = "operation", source = "operation", qualifiedByName = "mapEventOperationFromString")
    Event fromDbEntity(EventEntity eventEntity);

    @Named("mapTimestamp")
    default long mapTimestamp(Instant timestamp) {
        return timestamp.toEpochMilli();
    }

    @Named("mapEventTypeToString")
    default String mapEventTypeToString(EventType eventType) {
        return eventType.name();
    }

    @Named("mapEventOperationToString")
    default String mapEventOperationToString(EventOperation eventOperation) {
        return eventOperation.name();
    }

    @Named("mapEventTypeFromString")
    default EventType mapEventTypeFromString(String eventType) {
        switch (eventType) {
            case "LIKE":
                return EventType.LIKE;
            case "REVIEW":
                return EventType.REVIEW;
            case "FRIEND":
                return EventType.FRIEND;
            default:
                throw new RuntimeException("Внутренняя ошибка приложения, связанная с определением типа объекта, " +
                        "над которым производит действие пользователь. Если Вы видите это сообщение, " +
                        "пожалуйста, свяжитесь с разработчиками.");
        }
    }

    @Named("mapEventOperationFromString")
    default EventOperation mapEventOperationFromString(String eventOperation) {
        switch (eventOperation) {
            case "REMOVE":
                return EventOperation.REMOVE;
            case "ADD":
                return EventOperation.ADD;
            case "UPDATE":
                return EventOperation.UPDATE;
            default:
                throw new RuntimeException("Внутренняя ошибка приложения, связанная с определением вида операции над " +
                        "объектом, с которым взаимодействует пользователь. Если Вы видите это сообщение, " +
                        "пожалуйста, свяжитесь с разработчиками.");
        }
    }

}
