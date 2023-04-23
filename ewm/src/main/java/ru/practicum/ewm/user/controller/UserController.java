package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("Запрос пользователя: создание пользователя");
        return userService.create(userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Запрос пользователя: удаление пользователя");
        userService.delete(id);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer index,
                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос пользователя: получить всех пользователей");
        return userService.getUsers(ids, index, size);
    }

}
