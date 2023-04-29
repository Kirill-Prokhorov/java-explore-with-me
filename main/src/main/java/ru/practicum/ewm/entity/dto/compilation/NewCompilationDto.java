package ru.practicum.ewm.entity.dto.compilation;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;
    private Boolean pinned;
    @NotNull
    private String title;
}
