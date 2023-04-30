package ru.practicum.ewm.entity.dto.compilation;

import lombok.*;
import ru.practicum.ewm.entity.dto.event.EventShortDto;

import javax.validation.constraints.NotNull;
import java.util.Set;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Set<EventShortDto> events;
    @NotNull
    private Long id;
    @NotNull
    private Boolean pinned;
    @NotNull
    private String title;
}
