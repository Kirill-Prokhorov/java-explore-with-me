package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.dto.EventDto.LocationDto;
import ru.practicum.ewm.event.model.StateAction;


import static ru.practicum.ewm.util.Constants.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Jacksonized
public class EventUpdateDto {
    Long eventId;
    String annotation;
    String title;
    String description;
    State state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT)
    LocalDateTime eventDate;
    LocationDto location;
    StateAction stateAction;
    LocalDateTime published;
    Long category;
    Boolean paid;
    Integer participantLimit;

}
