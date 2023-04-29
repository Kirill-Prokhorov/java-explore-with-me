package ru.practicum.ewm.entity.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.entity.model.Location;
import ru.practicum.ewm.utils.DateTimePattern;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotNull
    @NotEmpty
    private String annotation;
    private Long category;
    @NotNull
    @NotEmpty
    private String description;
    @NotNull
    @JsonFormat(pattern = DateTimePattern.TIME_PATTERN, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    @NotNull
    @NotEmpty
    private String title;
}
