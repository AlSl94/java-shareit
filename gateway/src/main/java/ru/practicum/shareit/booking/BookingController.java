package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid BookingDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId,
                                               @RequestParam Boolean approved) {
        log.info("Updated booking={} by user={}", bookingId, userId);
        return bookingClient.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> bookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingStatus status = BookingStatus.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown status: " + state));
        log.info("Get booking with status {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.bookingsByBooker(userId, status, from, size);
    }

    @GetMapping(value = "/owner")
    public ResponseEntity<Object> bookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingStatus status = BookingStatus.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown status: " + state));
        log.info("Get booking by owner with status {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.bookingsByOwner(userId, status, from, size);
    }
}
