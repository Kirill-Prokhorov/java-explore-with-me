package ru.practicum.ewm.services.user;

import ru.practicum.ewm.entity.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(UserDto userDto);

    List<UserDto> findAllByIdIn(List<Long> ids, Integer from, Integer size);

    void deleteById(Long userId);
}
