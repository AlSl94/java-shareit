package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingDao extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = ?1 " +
            "AND b.start < current_timestamp AND b.end > current_timestamp ORDER BY b.start desc")
    List<Booking> findCurrentBookingsByBooker(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = ?1 " +
            "AND b.start > current_timestamp ORDER BY b.start desc")
    List<Booking> findFutureBookingsByBooker(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = ?1 AND b.status = 'WAITING'")
    List<Booking> findWaitingBookingsByBooker(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = ?1 AND b.status = 'REJECTED'")
    List<Booking> findRejectedBookingsByBooker(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.itemId = i.id WHERE i.owner = ?1 ORDER BY b.start desc")
    List<Booking> findBookingsByOwner(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.itemId = i.id WHERE i.owner = ?1 " +
            "AND b.start < current_timestamp AND b.end > current_timestamp ORDER BY b.start desc")
    List<Booking> findCurrentBookingsByOwner(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.itemId = i.id WHERE i.owner = ?1 " +
            "AND b.start > current_timestamp ORDER BY b.start desc")
    List<Booking> findFutureBookingsByOwner(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.itemId = i.id WHERE i.owner = ?1 " +
            "AND b.status = 'WAITING' ORDER BY b.start desc")
    List<Booking> findWaitingBookingsByOwner(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.itemId = i.id WHERE i.owner = ?1 " +
            "AND b.status = 'REJECTED' ORDER BY b.start desc")
    List<Booking> findRejectedBookingsByOwner(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.bookerId = ?1 AND b.end < current_timestamp ORDER BY b.start DESC")
    List<Booking> findPastBookingsByBooker(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.itemId = i.id WHERE i.owner = ?1 " +
            "AND b.end < current_timestamp ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwner(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.itemId = i.id " +
            "WHERE i.owner = ?1 AND i.id = ?2 ORDER BY b.start ASC")
    List<Booking> findLastBookingByItemId(Long userId, Long itemId);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.itemId = i.id " +
            "WHERE i.owner = ?1 AND i.id = ?2 ORDER BY b.start DESC")
    List<Booking> findNextBookingsByItemIdAndUserId(Long userId, Long itemId);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.itemId = i.id " +
            "WHERE b.bookerId = ?1 AND i.id = ?2 AND b.end < current_timestamp AND b.status = ?3")
    List<Booking> findCompletedBookings(Long bookerId, Long itemId, BookingStatus status);

}
