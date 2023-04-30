package ru.practicum.ewm.entity.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.entity.dto.category.CategoryDto;
import ru.practicum.ewm.entity.dto.user.UserShortDto;
import ru.practicum.ewm.entity.enums.Status;
import ru.practicum.ewm.entity.model.Location;
import ru.practicum.ewm.utils.DateTimePattern;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private long id;
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    @JsonFormat(pattern = DateTimePattern.TIME_PATTERN, shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = DateTimePattern.TIME_PATTERN, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = DateTimePattern.TIME_PATTERN, shape = JsonFormat.Shape.STRING)
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private Status state;
    private String title;
    private long views;
}
