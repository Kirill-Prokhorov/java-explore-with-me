package ru.practicum.ewm.entity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.entity.dto.event.EventFullDto;
import ru.practicum.ewm.entity.dto.event.EventShortDto;
import ru.practicum.ewm.entity.dto.event.NewEventDto;
import ru.practicum.ewm.entity.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "category.id", source = "category")
    Event fromNewDto(NewEventDto eventDto);

    EventFullDto toDto(Event event);

    EventShortDto toShortDto(Event event);
}
