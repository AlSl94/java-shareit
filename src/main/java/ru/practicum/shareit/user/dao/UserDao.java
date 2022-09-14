package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserDao {

    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    UserDto getUser(Long userId);

    List<UserDto> getAll();

    void delete(Long userId);
}
