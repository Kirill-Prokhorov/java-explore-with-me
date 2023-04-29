package ru.practicum.ewm.entity.dto.compilation;

import lombok.*;

import java.util.Set;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;
    private String title;
}
