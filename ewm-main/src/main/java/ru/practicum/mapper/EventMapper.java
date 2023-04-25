package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.event.CreateEventDto;
import ru.practicum.dto.event.FullEventDto;
import ru.practicum.dto.event.ShortEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.User;
import ru.practicum.model.event.Event;

@Mapper
public interface EventMapper {

    EventMapper EVENT_MAPPER = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", expression = "java(category)")
    @Mapping(target = "eventState", constant = "PENDING")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", expression = "java(java.time.LocalDateTime.now())")
    Event toEventFromCreateDto(User initiator, Category category, CreateEventDto createEventDto);

    @Mapping(target = "createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "publishedOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "views", expression = "java(0L)")
    @Mapping(target = "state", expression = "java(event.getEventState().name())")
    FullEventDto toFullEventDto(Event event);

    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    ShortEventDto toShortEventDto(Event event);

    ShortEventDto toShortFromFull(FullEventDto fullEventDto);
}
