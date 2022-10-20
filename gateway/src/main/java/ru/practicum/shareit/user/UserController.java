package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utility.Create;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("User created=id{}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody UserDto userDto) {
        log.info("User {} updated: {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping(value = {"/{userId}"})
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Got user {}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Got all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping(value = "{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("User {} deleted", userId);
        return userClient.delete(userId);
    }
}
