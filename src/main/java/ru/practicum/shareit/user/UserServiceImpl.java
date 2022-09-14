package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDaoImplInMemory;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDaoImplInMemory userDao;

    @Override
    public UserDto create(UserDto userDto) {
        userDao.checkEmail(userDto);
        return userDao.create(userDto);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        UserDto updatedUser = getUser(userId);
        if (userDto.getName() != null) updatedUser.setName(userDto.getName());
        if (userDto.getEmail() != null && !userDto.getEmail().equals(updatedUser.getEmail())) {
            userDao.checkEmail(userDto);
            updatedUser.setEmail(userDto.getEmail());
        }
        return userDao.update(userId, updatedUser);
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
