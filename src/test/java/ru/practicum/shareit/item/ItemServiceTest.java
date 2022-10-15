package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dao.CommentDao;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {
    private ItemService itemService;
    private CommentService commentService;
    private ItemDao itemDao;
    private UserDao userDao;
    private CommentDao commentDao;

    private BookingDao bookingDao;

    @BeforeEach
    void beforeEach() {
        itemDao = mock(ItemDao.class);
        userDao = mock(UserDao.class);
        commentDao = mock(CommentDao.class);
        bookingDao = mock(BookingDao.class);
        UserService userService = new UserService(userDao);
        itemService = new ItemService(itemDao, bookingDao, commentDao, userService);
        commentService = new CommentService(commentDao, userService, itemService, bookingDao);
    }

    @Test
    void getItemByIdTest() {
        Item item = item();
        Long itemId = item.getId();
        when(itemDao.findById(itemId))
                .thenReturn(Optional.of(item));
        ItemInfoDto infoDto = itemService.getItem(item.getOwner(), itemId);
        assertNotNull(infoDto);
        assertEquals("item1", infoDto.getName());
        verify(itemDao, times(1)).findById(itemId);
    }

    @Test
    void getItemByWrongIdTest() {
        Item item = item();
        Long itemId = item.getId();
        long incorrectId = (long) (Math.random() * 100) + itemId + 3;
        when(itemDao.findById(incorrectId))
                .thenThrow(new WrongParameterException("Wrong id"));
        Throwable thrown = assertThrows(WrongParameterException.class,
                () -> itemService.getItem(incorrectId, item.getOwner()));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void createItemTest() {
        when(itemDao.save(any()))
                .thenReturn(item());
        when(userDao.findById(any()))
                .thenReturn(Optional.of(user()));

        ItemDto itemDto = itemService.create(item().getOwner(), ItemMapper.toItemDto(item()));

        assertNotNull(itemDto);
        assertEquals("item1", itemDto.getName());
        assertEquals("description1", itemDto.getDescription());
        assertEquals(item().getId(), itemDto.getId());
        verify(itemDao, times(1)).save(any());
    }

    @Test
    void updateItemTest() {
        when(itemDao.save(any()))
                .thenReturn(item());
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item()));
        when(userDao.findById(any()))
                .thenReturn(Optional.of(user()));

        Item item = item();

        ItemDto itemDto = itemService.create(item.getOwner(), ItemMapper.toItemDto(item));
        item.setName("updatedItem");
        ItemDto updatedDto = itemService.update(item.getOwner(), item.getId(), ItemMapper.toItemDto(item));
        assertNotNull(itemDto);
        assertEquals("updatedItem", updatedDto.getName());
    }

    @Test
    void getUserItemsTest() {
        when(itemDao.save(any()))
                .thenReturn(item());
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item()));
        when(userDao.findById(any()))
                .thenReturn(Optional.of(user()));
        when(itemDao.findItemsByOwner(any(), any()))
                .thenReturn(Collections.singletonList(item()));
        when(commentDao.findAllByItemId(anyLong()))
                .thenReturn(Collections.singletonList(comment()));
        when(bookingDao.findLastBookingByItemId(anyLong(), anyLong()))
                .thenReturn(Collections.singletonList(booking()));
        when(bookingDao.findNextBookingsByItemIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Collections.singletonList(booking()));

        List<ItemInfoDto> dtos = itemService.getUserItems(item().getOwner(), 0, 10);

        assertNotNull(dtos);
        assertEquals("item1", dtos.get(0).getName());
    }

    @Test
    void searchItemsTest() {
        when(itemDao.save(any()))
                .thenReturn(item());
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item()));
        when(userDao.findById(any()))
                .thenReturn(Optional.of(user()));
        when(itemDao.findItemsByText(any(), any())).thenReturn(Collections.singletonList(item()));

        List<ItemDto> dtos = itemService.search("description1", 0, 10);

        assertNotNull(dtos);
        assertEquals("item1", dtos.get(0).getName());
    }

    @Test
    void getSimpleItemTest() {
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item()));
        ItemDto dto = itemService.getSimpleItem(item().getId());
        assertNotNull(dto);
        assertEquals("item1", dto.getName());
    }

    @Test
    void createItemValidationUserIdIsNullTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item());

        when(itemDao.save(any()))
                .thenReturn(item());

        assertThrows(ValidationException.class,
                () -> itemService.create(null, itemDto));
    }

    @Test
    void createItemValidationUserIsNotExists() {
        ItemDto itemDto = ItemMapper.toItemDto(item());
        User user = user();
        user.setId(null);

        when(itemDao.save(any()))
                .thenReturn(item());

        assertThrows(WrongParameterException.class,
                () -> itemService.create(1L, itemDto));
    }

    @Test
    void createItemValidationAvailableIsNullTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item());
        itemDto.setAvailable(null);

        when(itemDao.save(any()))
                .thenReturn(item());
        when(userDao.findById(anyLong()))
                .thenReturn(Optional.of(user()));

        assertThrows(ValidationException.class,
                () -> itemService.create(1L, itemDto));
    }

    @Test
    void createItemValidationNameIsNullTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item());
        itemDto.setName(null);

        when(itemDao.save(any()))
                .thenReturn(item());
        when(userDao.findById(anyLong()))
                .thenReturn(Optional.of(user()));

        assertThrows(ValidationException.class,
                () -> itemService.create(1L, itemDto));
    }

    @Test
    void createItemValidationDescriptionIsNullTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item());
        itemDto.setDescription(null);

        when(itemDao.save(any()))
                .thenReturn(item());
        when(userDao.findById(anyLong()))
                .thenReturn(Optional.of(user()));

        assertThrows(ValidationException.class,
                () -> itemService.create(1L, itemDto));
    }

    @Test
    void createCommentOnItemTest() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment());
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item()));
        when(userDao.findById(anyLong()))
                .thenReturn(Optional.of(user()));
        when(commentDao.save(any(Comment.class)))
                .thenReturn(comment());
        when(bookingDao.findBookingsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(Collections.singletonList(booking()));
        CommentDto comment = commentService.postComment(2L, 1L, commentDto);
        assertNotNull(comment);
        assertEquals(commentDto.getId(), comment.getId());
    }

    @Test
    void createCommentOnItemWithoutBookingTest() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment());
        when(userDao.findById(anyLong()))
                .thenReturn(Optional.of(user()));
        when(commentDao.save(any(Comment.class)))
                .thenReturn(comment());
        assertThrows(ValidationException.class,
                () -> commentService.postComment(2L, 1L, commentDto));
    }

    @Test
    void createCommentOnItemBlankTextTest() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment());
        commentDto.setText("");
        when(userDao.findById(anyLong()))
                .thenReturn(Optional.of(user()));
        when(commentDao.save(any(Comment.class)))
                .thenReturn(comment());
        assertThrows(ValidationException.class,
                () -> commentService.postComment(2L, 1L, commentDto));
    }

    @Test
    void itemMapperTest() {
        ItemDto dto = ItemMapper.toItemDto(item());
        List<ItemDto> dtos = Collections.singletonList(dto);
        assertNotNull(ItemMapper.toItemList(dtos));
    }

    private Item item() {
        return new Item(1L, "item1", "description1",
                true, user().getId(), 2L);
    }

    private User user() {
        return new User(1L, "user1", "user1@mail.ru");
    }

    private User booker() {
        return new User(2L, "user2", "user2@mail.ru");
    }

    private Comment comment() {
        return new Comment(1L, "comment1", item(), booker(), LocalDateTime.now());
    }

    private Booking booking() {
        return new Booking(1L, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(1),
                BookingStatus.APPROVED, item().getId(), booker().getId());
    }
}