package ru.practicum.ewm.entity.dto.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    @NotNull
    @Email
    private String email;
    @NotNull
    @NotEmpty
    private String name;
}
