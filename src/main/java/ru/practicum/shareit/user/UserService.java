package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    UserDto getUser(Long userId);

    List<UserDto> getAll();

    void delete(Long userId);
}
