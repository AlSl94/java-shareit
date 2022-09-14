package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto create(UserDto userDto) {
        return userDao.create(userDto);
    }
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        return userDao.update(userId, userDto);
    }
    @Override
    public UserDto getUser(Long userId) {
        return userDao.getUser(userId);
    }
    @Override
    public List<UserDto> getAll() {
        return userDao.getAll();
    }
    @Override
    public void delete(Long userId) {
        userDao.delete(userId);
    }
}
