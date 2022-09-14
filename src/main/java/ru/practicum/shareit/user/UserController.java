package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping
    public UserDto create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        UserDto user = userServiceImpl.create(userDto);
        log.info("Пользователь с id {} создан", user.getId());
        return user;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto user = userServiceImpl.update(userId, userDto);
        log.info("Пользователь с id {} обновлен", user.getId());
        return user;
    }

    @GetMapping(value = {"/{userId}"})
    public UserDto getUser(@PathVariable Long userId) {
        UserDto user = userServiceImpl.getUser(userId);
        log.info("Пользователь с id {} получен", user.getId());
        return user;
    }

    @GetMapping
    List<UserDto> getAll() {
        List<UserDto> users = userServiceImpl.getAll();
        log.info("Пользователи получены");
        return users;
    }

    @DeleteMapping(value = "{userId}")
    public void delete(@PathVariable Long userId) {
        userServiceImpl.delete(userId);
        log.info("Пользователь с id {} удален", userId);
    }
}
