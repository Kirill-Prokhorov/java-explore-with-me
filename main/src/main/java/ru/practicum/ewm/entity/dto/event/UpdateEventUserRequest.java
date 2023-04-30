package ru.practicum.ewm.entity.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.entity.enums.StateAction;
import ru.practicum.ewm.entity.model.Location;
import ru.practicum.ewm.utils.DateTimePattern;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {
    private Long category;
    private String annotation;
    private String description;
    @JsonFormat(pattern = DateTimePattern.TIME_PATTERN, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;
}
