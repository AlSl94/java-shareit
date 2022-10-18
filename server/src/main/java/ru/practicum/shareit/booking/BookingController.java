package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.FullBookingDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public FullBookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody BookingDto bookingDto) {
        FullBookingDto dto = bookingService.create(userId, bookingDto);
        log.info("Бронь создана");
        return dto;
    }

    @PatchMapping(value = "/{bookingId}")
    public FullBookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam Boolean approved) {
        FullBookingDto dto = bookingService.updateStatus(userId, bookingId, approved);
        log.info("Бронь обновлено");
        return dto;
    }

    @GetMapping(value = "/{bookingId}")
    public FullBookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        FullBookingDto dto = bookingService.getBooking(userId, bookingId);
        log.info("Бронь с id {} получена", bookingId);
        return dto;
    }

    @GetMapping
    public List<FullBookingDto> bookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        List<FullBookingDto> bookings = bookingService.bookingsByBooker(userId, state, from, size);
        log.info("Получен список всех бронирований текущего пользователя");
        return bookings;
    }

    @GetMapping(value = "/owner")
    public List<FullBookingDto> bookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        List<FullBookingDto> bookings = bookingService.bookingsByOwner(userId, state, from, size);
        log.info("Получен список всех забронированных вещей текущего пользователя");
        return bookings;
    }
}
