package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserDao dao;

    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        dao.save(user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = UserMapper.toUser(getUser(userId));
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
        dao.save(user);
        return UserMapper.toUserDto(user);
    }

    public UserDto getUser(Long userId) {
        User user = dao.findById(userId).orElseThrow(() -> new WrongParameterException("User не найден"));
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getAll() {
        return UserMapper.toUserDtoList(dao.findAll());
    }

    @Transactional
    public void delete(Long userId) {
        dao.deleteById(userId);
    }
}
