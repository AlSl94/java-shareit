package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        userService = new UserService(userDao);
    }

    @Test
    void createUserTest() {
        User user = createUser().get(0);
        when(userDao.save(user))
                .thenReturn(user);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));

        assertNotNull(userDto);
        assertEquals("user1", userDto.getName());
        assertEquals("user1@mail.ru", userDto.getEmail());
        assertEquals(user.getId(), userDto.getId());
    }

    @Test
    void updateUserTest() {

        User user1 = createUser().get(0);
        User user2 = createUser().get(1);
        Long id = user1.getId();
        userDao.save(user1);

        when(userDao.save(any()))
                .thenReturn(user2);
        when(userDao.findById(id))
                .thenReturn(Optional.of(user1));
        UserDto userDto = userService.update(id, UserMapper.toUserDto(user2));

        assertNotNull(userDto);
        assertEquals("user2", userDto.getName());
        assertEquals("user2@mail.ru", userDto.getEmail());
        assertEquals(id, userDto.getId());
    }

    @Test
    void updateUserTestWithoutUser() {

        User user1 = createUser().get(0);
        when(userDao.save(any(User.class)))
                .thenReturn(user1);

        assertThrows(WrongParameterException.class,
                () -> userService.update(1L, UserMapper.toUserDto(user1)));
    }

    @Test
    void getUserTest() {
        User user = createUser().get(0);
        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        UserDto userDto = userService.getUser(user.getId());

        assertNotNull(userDto);
        assertEquals("user1", userDto.getName());
        assertEquals("user1@mail.ru", userDto.getEmail());
        verify(userDao, times(1)).findById(user.getId());
    }

    @Test
    void getUserWithWrongIdTest() {
        when(userDao.findById(10L))
                .thenThrow(new WrongParameterException("User не найден"));
        Throwable thrown = assertThrows(WrongParameterException.class,
                () -> userService.getUser(10L));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllUsersTest() {

        User user1 = createUser().get(0);
        User user3 = createUser().get(2);

        when(userDao.findAll()).thenReturn(List.of(user1, user3));
        List<UserDto> users = userService.getAll();

        assertNotNull(users);
        assertEquals("user1", users.get(0).getName());
        assertEquals("user3@mail.ru", users.get(1).getEmail());
    }

    @Test
    void deleteUserTest() {
        User user = createUser().get(0);
        userService.delete(user.getId());
        verify(userDao, times(1)).deleteById(user.getId());
    }

    private List<User> createUser() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(1L, "user1", "user1@mail.ru"));
        users.add(new User(1L, "user2", "user2@mail.ru"));
        users.add(new User(3L, "user3", "user3@mail.ru"));
        return users;
    }
}