package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.FullBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dao.CommentDao;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class BookingServiceTest {

    private BookingService bookingService;
    private BookingDao bookingDao;
    private UserDao userDao;
    private ItemDao itemDao;

    private User owner;
    private User booker;

    private Item item;

    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "user1", "user1@mail.ru");
        booker = new User(2L, "user2", "user2@mail.ru");
        item = new Item(1L, "item", "description",
                true, owner.getId(), null);
        booking = new Booking(1L, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING, item.getId(), booker.getId());
        bookingDao = mock(BookingDao.class);
        userDao = mock(UserDao.class);
        itemDao = mock(ItemDao.class);
        CommentDao commentDao = mock(CommentDao.class);
        UserService userService = new UserService(userDao);
        ItemService itemService = new ItemService(itemDao, bookingDao, commentDao, userService);
        bookingService = new BookingService(bookingDao, userService, itemService);
    }

    @Test
    void createBookingTest() {
        when(userDao.findById(any()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.save(any(Booking.class)))
                .thenReturn(booking);

        FullBookingDto bookingDto = bookingService.create(booker.getId(), BookingMapper.toBookingDto(booking));

        assertNotNull(bookingDto);
        assertEquals("item", bookingDto.getItem().getName());
        assertEquals("user2", bookingDto.getBooker().getName());
        assertEquals(booking.getId(), bookingDto.getId());
    }

    @Test
    void updateBookingStatusTest() {
        when(userDao.findById(any()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.save(any(Booking.class)))
                .thenReturn(booking);
        when(bookingDao.findById(any())).thenReturn(Optional.ofNullable(booking));

        FullBookingDto updatedBooking = bookingService.updateStatus(owner.getId(), booking.getId(), false);

        assertNotNull(updatedBooking);
        assertEquals(BookingStatus.REJECTED, updatedBooking.getStatus());
    }

    @Test
    void getBookingTest() {
        when(userDao.findById(any()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.save(any(Booking.class)))
                .thenReturn(booking);
        when(bookingDao.findById(any())).thenReturn(Optional.ofNullable(booking));

        FullBookingDto dto = bookingService.getBooking(owner.getId(), booking.getId());

        assertNotNull(dto);
        assertEquals("user2", dto.getBooker().getName());
    }


    @Test
    void bookingsByOwnerCurrentTest() {
        Booking updatedBooking = booking;
        updatedBooking.setStart(LocalDateTime.now().minusHours(1));
        updatedBooking.setEnd(LocalDateTime.now().plusDays(1));

        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.findById(booking.getId()))
                .thenReturn(Optional.of(updatedBooking));
        when(bookingDao.findCurrentBookingsByOwner(any(), any()))
                .thenReturn(Collections.singletonList(updatedBooking));

        List<FullBookingDto> list = bookingService.bookingsByOwner(owner.getId(), "CURRENT", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByOwnerFutureTest() {
        Booking updatedBooking = booking;
        updatedBooking.setStart(LocalDateTime.now().plusHours(1));
        updatedBooking.setEnd(LocalDateTime.now().plusDays(1));

        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.findById(booking.getId()))
                .thenReturn(Optional.of(updatedBooking));
        when(bookingDao.findFutureBookingsByOwner(any(), any()))
                .thenReturn(Collections.singletonList(updatedBooking));
        List<FullBookingDto> list = bookingService.bookingsByOwner(owner.getId(), "FUTURE", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByOwnerPastTest() {
        Booking updatedBooking = booking;
        updatedBooking.setStart(LocalDateTime.now().minusDays(10));
        updatedBooking.setEnd(LocalDateTime.now().minusDays(5));

        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.findById(booking.getId()))
                .thenReturn(Optional.of(updatedBooking));
        when(bookingDao.findPastBookingsByOwner(any(), any()))
                .thenReturn(Collections.singletonList(updatedBooking));
        List<FullBookingDto> list = bookingService.bookingsByOwner(owner.getId(), "PAST", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByOwnerWaitingTest() {
        Booking updatedBooking = booking;
        updatedBooking.setStatus(BookingStatus.WAITING);

        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.findById(booking.getId()))
                .thenReturn(Optional.of(updatedBooking));
        when(bookingDao.findWaitingBookingsByOwner(any(), any()))
                .thenReturn(Collections.singletonList(updatedBooking));
        List<FullBookingDto> list = bookingService.bookingsByOwner(owner.getId(), "WAITING", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByOwnerRejectedTest() {
        Booking updatedBooking = booking;
        updatedBooking.setStatus(BookingStatus.REJECTED);

        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.findById(booking.getId()))
                .thenReturn(Optional.of(updatedBooking));
        when(bookingDao.findRejectedBookingsByOwner(any(), any()))
                .thenReturn(Collections.singletonList(updatedBooking));
        List<FullBookingDto> list = bookingService.bookingsByOwner(owner.getId(), "REJECTED", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByOwnerAllTest() {
        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.save(any(Booking.class)))
                .thenReturn(booking);
        when(bookingDao.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));
        when(bookingDao.findBookingsByOwner(any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<FullBookingDto> list = bookingService.bookingsByOwner(owner.getId(), "ALL", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByOwnerWrongStateTest() {
        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.save(any(Booking.class)))
                .thenReturn(booking);
        when(bookingDao.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));
        assertThrows(ValidationException.class,
                () -> bookingService.bookingsByOwner(booker.getId(), "sfsdf", 0, 20));
    }

    @Test
    void bookingsByBookerCurrentTest() {
        Booking updatedBooking = booking;
        updatedBooking.setStart(LocalDateTime.now().minusHours(1));
        updatedBooking.setEnd(LocalDateTime.now().plusDays(1));

        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.findById(booking.getId()))
                .thenReturn(Optional.of(updatedBooking));
        when(bookingDao.findCurrentBookingsByBooker(any(), any()))
                .thenReturn(Collections.singletonList(updatedBooking));

        List<FullBookingDto> list = bookingService.bookingsByBooker(owner.getId(), "CURRENT", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByBookerFutureTest() {
        Booking updatedBooking = booking;
        updatedBooking.setStart(LocalDateTime.now().plusHours(1));
        updatedBooking.setEnd(LocalDateTime.now().plusDays(1));

        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.findById(booking.getId()))
                .thenReturn(Optional.of(updatedBooking));
        when(bookingDao.findFutureBookingsByBooker(any(), any()))
                .thenReturn(Collections.singletonList(updatedBooking));
        List<FullBookingDto> list = bookingService.bookingsByBooker(owner.getId(), "FUTURE", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByBookerPastTest() {
        Booking updatedBooking = booking;
        updatedBooking.setStart(LocalDateTime.now().minusDays(10));
        updatedBooking.setEnd(LocalDateTime.now().minusDays(5));

        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.findById(booking.getId()))
                .thenReturn(Optional.of(updatedBooking));
        when(bookingDao.findPastBookingsByBooker(any(), any()))
                .thenReturn(Collections.singletonList(updatedBooking));
        List<FullBookingDto> list = bookingService.bookingsByBooker(owner.getId(), "PAST", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByBookerWaitingTest() {
        Booking updatedBooking = booking;
        updatedBooking.setStatus(BookingStatus.WAITING);

        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.findById(booking.getId()))
                .thenReturn(Optional.of(updatedBooking));
        when(bookingDao.findWaitingBookingsByBooker(any(), any()))
                .thenReturn(Collections.singletonList(updatedBooking));
        List<FullBookingDto> list = bookingService.bookingsByBooker(owner.getId(), "WAITING", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByBookerRejectedTest() {
        Booking updatedBooking = booking;
        updatedBooking.setStatus(BookingStatus.REJECTED);

        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.findById(booking.getId()))
                .thenReturn(Optional.of(updatedBooking));
        when(bookingDao.findRejectedBookingsByBooker(any(), any()))
                .thenReturn(Collections.singletonList(updatedBooking));
        List<FullBookingDto> list = bookingService.bookingsByBooker(owner.getId(), "REJECTED", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByBookerAllTest() {
        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.save(any(Booking.class)))
                .thenReturn(booking);
        when(bookingDao.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));
        when(bookingDao.findBookingsByBookerIdOrderByStartDesc(any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<FullBookingDto> list = bookingService.bookingsByBooker(booker.getId(), "ALL", 0, 20);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void bookingsByBookerWrongStateTest() {
        when(userDao.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userDao.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingDao.save(any(Booking.class)))
                .thenReturn(booking);
        when(bookingDao.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));
        assertThrows(ValidationException.class,
                () -> bookingService.bookingsByBooker(booker.getId(), "sfsdf", 0, 20));
    }

    @Test
    void bookingValidationNoUserTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        assertThrows(WrongParameterException.class,
                () -> bookingService.create(booker.getId(), bookingDto));
    }

    @Test
    void bookingValidationBookerIsOwnerTest() {
        when(userDao.findById(any()))
                .thenReturn(Optional.of(owner));
        assertThrows(WrongParameterException.class,
                () -> bookingService.create(owner.getId(), BookingMapper.toBookingDto(booking)));
    }

    @Test
    void bookingValidationItemIsNullTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        when(userDao.findById(any()))
                .thenReturn(Optional.of(owner));
        bookingDto.setItemId(null);
        assertThrows(WrongParameterException.class,
                () -> bookingService.create(owner.getId(), bookingDto));
    }

    @Test
    void bookingValidationEndTimeInPastTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        when(userDao.findById(any()))
                .thenReturn(Optional.of(booker));
        bookingDto.setEnd(LocalDateTime.now().minusDays(10));
        assertThrows(ValidationException.class,
                () -> bookingService.create(owner.getId(), bookingDto));
    }

    @Test
    void bookingValidationStartTimeInPastTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        when(userDao.findById(any()))
                .thenReturn(Optional.of(booker));
        bookingDto.setStart(LocalDateTime.now().minusDays(10));
        assertThrows(ValidationException.class,
                () -> bookingService.create(owner.getId(), bookingDto));
    }

    @Test
    void bookingValidationEndTimeIsBeforeStartTimeTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        when(userDao.findById(any()))
                .thenReturn(Optional.of(booker));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        assertThrows(ValidationException.class,
                () -> bookingService.create(owner.getId(), bookingDto));
    }

    @Test
    void bookingValidationItemIsNotAvailableTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        Item busyItem = item;
        busyItem.setAvailable(false);

        when(userDao.findById(any()))
                .thenReturn(Optional.of(booker));
        when(itemDao.findById(anyLong()))
                .thenReturn(Optional.of(busyItem));
        assertThrows(ValidationException.class,
                () -> bookingService.create(owner.getId(), bookingDto));
    }
}