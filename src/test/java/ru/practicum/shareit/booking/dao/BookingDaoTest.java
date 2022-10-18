package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingDaoTest {
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private BookingDao bookingDao;

    private Item item;
    private User owner;
    private User booker;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        owner = userDao.save(new User(1L, "user1", "user1@mail.ru"));
        booker = userDao.save(new User(2L, "user2", "user2@mail.ru"));
        item = itemDao.save(new Item(1L, "item", "description",
                true, owner.getId(), null));
        booking = bookingDao.save(new Booking(1L, LocalDateTime.now().plusMinutes(10),
                LocalDateTime.now().plusDays(1), BookingStatus.APPROVED, item.getId(), booker.getId()));
    }

    @AfterEach
    void afterEach() {
        userDao.deleteAll();
        itemDao.deleteAll();
        bookingDao.deleteAll();
    }

    @Test
    void findBookingsByBookerIdOrderByStartDescTest() {
        var bookings = bookingDao.findBookingsByBookerIdOrderByStartDesc(booker.getId(),
                Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findCurrentBookingsByBookerTest() {
        booking.setStart(LocalDateTime.now().minusMinutes(1));
        bookingDao.save(booking);
        var bookings = bookingDao.findCurrentBookingsByBooker(booker.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findFutureBookingsByBookerTest() {
        var bookings = bookingDao.findFutureBookingsByBooker(booker.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findWaitingBookingsByBookerTest() {
        booking.setStatus(BookingStatus.WAITING);
        var bookings = bookingDao.findWaitingBookingsByBooker(booker.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findRejectedBookingsByBookerTest() {
        booking.setStatus(BookingStatus.REJECTED);
        var bookings = bookingDao.findRejectedBookingsByBooker(booker.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findBookingsByOwnerTest() {
        var bookings = bookingDao.findBookingsByOwner(owner.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));

    }

    @Test
    void findCurrentBookingsByOwnerTest() {
        booking.setStart(LocalDateTime.now().minusHours(1));
        var bookings = bookingDao.findCurrentBookingsByOwner(owner.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findFutureBookingsByOwnerTest() {
        var bookings = bookingDao.findFutureBookingsByOwner(owner.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findWaitingBookingsByOwnerTest() {
        booking.setStatus(BookingStatus.WAITING);
        var bookings = bookingDao.findWaitingBookingsByOwner(owner.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findRejectedBookingsByOwnerTest() {
        booking.setStatus(BookingStatus.REJECTED);
        var bookings = bookingDao.findRejectedBookingsByOwner(owner.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findPastBookingsByBookerTest() {
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingDao.save(booking);
        var bookings = bookingDao.findPastBookingsByBooker(booker.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findPastBookingsByOwnerTest() {
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingDao.save(booking);
        var bookings = bookingDao.findPastBookingsByOwner(owner.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findLastBookingByItemIdTest() {
        var bookings = bookingDao.findLastBookingByItemId(owner.getId(), item.getId());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findNextBookingsByItemIdAndUserIdTest() {
        var bookings = bookingDao.findNextBookingsByItemIdAndUserId(owner.getId(), item.getId());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }
}