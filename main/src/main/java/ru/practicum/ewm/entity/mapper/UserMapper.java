package ru.practicum.ewm.entity.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.entity.dto.user.UserDto;
import ru.practicum.ewm.entity.dto.user.UserShortDto;
import ru.practicum.ewm.entity.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User fromDto(UserDto userDto);

    UserShortDto toShortDto(User user);
}
