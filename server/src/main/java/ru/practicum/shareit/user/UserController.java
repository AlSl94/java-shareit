package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        UserDto user = userService.create(userDto);
        log.info("Пользователь с id {} создан", user.getId());
        return user;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto user = userService.update(userId, userDto);
        log.info("Пользователь с id {} обновлен", user.getId());
        return user;
    }

    @GetMapping(value = {"/{userId}"})
    public UserDto getUser(@PathVariable Long userId) {
        UserDto user = userService.getUser(userId);
        log.info("Пользователь с id {} получен", user.getId());
        return user;
    }

    @GetMapping
    List<UserDto> getAll() {
        List<UserDto> users = userService.getAll();
        log.info("Пользователи получены");
        return users;
    }

    @DeleteMapping(value = "{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
        log.info("Пользователь с id {} удален", userId);
    }
}
