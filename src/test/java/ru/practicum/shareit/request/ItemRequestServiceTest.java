package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utility.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class ItemRequestServiceTest {
    private ItemRequestDao requestDao;
    private UserDao userDao;
    private ItemDao itemDao;
    private ItemRequestService requestService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        requestDao = mock(ItemRequestDao.class);
        itemDao = mock(ItemDao.class);
        UserService userService = new UserService(userDao);
        requestService = new ItemRequestService(requestDao, itemDao, userService);
    }

    @Test
    void createItemRequestTest() {
        when(requestDao.save(any(ItemRequest.class))).thenReturn(request());
        when(userDao.findById(any())).thenReturn(Optional.of(user()));
        ItemRequestDto dto = requestService.create(2L,
                ItemRequestMapper.toItemRequestDto(request()));
        assertNotNull(dto);
        assertEquals("itemRequest1", dto.getDescription());
        assertEquals(1L, dto.getId());
        verify(requestDao, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getItemRequestByIdTest() {

        when(requestDao.findById(any())).thenReturn(Optional.of(request()));
        when(userDao.findById(any())).thenReturn(Optional.of(user()));

        ItemRequestDtoWithItems dto = requestService.getItemRequestById(2L, 1L);

        assertNotNull(dto);
        assertEquals("itemRequest1", dto.getDescription());
        assertEquals(1L, dto.getId());
        verify(requestDao, times(1)).findById(request().getId());
    }

    @Test
    void getWrongItemRequestByIdTest() {

        when(requestDao.findById(10L)).thenThrow(new WrongParameterException("Неверный id"));
        when(userDao.findById(any())).thenReturn(Optional.of(user()));

        Throwable thrown = assertThrows(WrongParameterException.class,
                () -> requestService.getItemRequestById(2L, 10L));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void getUserItemRequestsTest() {
        when(userDao.findById(any()))
                .thenReturn(Optional.of(user()));
        when(requestDao.findAllByRequestorIdOrderByCreatedDesc(any()))
                .thenReturn(List.of(request()));

        List<ItemRequestDtoWithItems> dtos = requestService.getUserItemRequests(2L);

        assertNotNull(dtos);
        assertEquals("itemRequest1", dtos.get(0).getDescription());
    }

    @Test
    void getAllItemRequestsTest() {

        when(userDao.findById(any()))
                .thenReturn(Optional.of(user()));
        when(itemDao.findAllByRequestId(any()))
                .thenReturn(Collections.singletonList(item()));
        Pageable pageable = FromSizeRequest.of(0, 10);
        when(requestDao.findAll(pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(request()),
                        PageRequest.of(0, 10, Sort.unsorted()), 0));

        List<ItemRequestDtoWithItems> dtos = requestService.getAllItemRequests(1L, 0, 10);

        assertNotNull(dtos);
        assertEquals("itemRequest1", dtos.get(0).getDescription());
    }

    @Test
    void getAllItemRequestsWithoutUserTest() {
        assertThrows(WrongParameterException.class,
                () -> requestService.getAllItemRequests(2L, 0, 10));
    }

    private ItemRequest request() {
        return new ItemRequest(1L, "itemRequest1", 2L, LocalDateTime.now());
    }

    private Item item() {
        return new Item(1L, "item1", "itemDescription1", true, 1L, 1L);
    }

    private User user() {
        return new User(2L, "User1", "User1@mail.ru");
    }
}