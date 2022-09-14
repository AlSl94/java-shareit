package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
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
        checkEmail(userDto);
        User user = UserMapper.toUser(userDto);
        user.setId(++id);
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        UserDto updatedUser = getUser(userId);
        if (userDto.getName() != null) updatedUser.setName(userDto.getName());
        if (userDto.getEmail() != null && !userDto.getEmail().equals(updatedUser.getEmail())) {
            checkEmail(userDto);
            updatedUser.setEmail(userDto.getEmail());
        }
        User user = UserMapper.toUser(updatedUser);
        users.put(userId, user);
        return updatedUser;
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

    private void checkEmail(UserDto userDto) {
        if (users.values().stream()
                .map(User::getEmail)
                .anyMatch(str -> str.equals(userDto.getEmail()))) {
            throw new RuntimeException(String.format("Юзер с email %s уже существует.", userDto.getEmail()));
        }
    }
}