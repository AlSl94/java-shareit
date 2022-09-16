package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserDaoImplInMemory implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(++id);
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        users.put(userId, user);
        return userDto;
    }

    @Override
    public UserDto getUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new WrongParameterException("Пользователя не существует");
        }
        return UserMapper.toUserDto(users.get(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        if (!users.containsKey(userId)) {
            throw new WrongParameterException("Несуществующий id");
        }
        users.remove(userId);
    }

    public void checkEmail(UserDto userDto) {
        if (users.values().stream()
                .map(User::getEmail)
                .anyMatch(str -> str.equals(userDto.getEmail()))) {
            throw new ConflictException(String.format("Юзер с email %s уже существует.", userDto.getEmail()));
        }
    }
}