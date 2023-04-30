package ru.practicum.ewm.entity.dto.category;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @NotEmpty
    @NotEmpty
    private String name;
}