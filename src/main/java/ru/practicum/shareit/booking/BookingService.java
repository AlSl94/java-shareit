package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.FullBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingDao dao;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    public BookingDto create(Long userId, BookingDto bookingDto) {

        createValidation(userId, bookingDto);
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBookerId(userId);
        booking.setStatus(BookingStatus.WAITING);
        dao.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    public FullBookingDto updateStatus(Long userId, Long bookingId, Boolean approved) {

        Booking booking = dao.findById(bookingId).orElseThrow(() -> new WrongParameterException("Booking не найден"));

        if (!Objects.equals(userId, itemService.getSimpleItem(booking.getItemId()).getOwner())) {
            throw new WrongParameterException("Изменять бронь может только владелец предмета");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Статус уже APPROVED");
        }

        booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        dao.save(booking);

        return getBooking(userId, bookingId);
    }

    public FullBookingDto getBooking(Long userId, Long bookingId) {

        if (userService.getUser(userId) == null) {
            throw new WrongParameterException("User не найден");
        }

        Booking booking = dao.findById(bookingId).orElseThrow(() -> new WrongParameterException("Booking не найден"));

        if (!Objects.equals(booking.getBookerId(), userId)
                && !Objects.equals(itemService.getSimpleItem(booking.getItemId()).getOwner(), userId)) {
            throw new WrongParameterException("Получить информацию может только владелец или арендатор предмета");
        }

        return BookingMapper.toFullBookingDto(booking,
                userService.getUser(booking.getBookerId()),
                itemService.getSimpleItem(booking.getItemId()));
    }

    public List<FullBookingDto> bookingsByBooker(Long userId, String state) {

        if (userService.getUser(userId) == null) {
            throw new WrongParameterException("User не существует");
        }

        List<Booking> bookings;
        List<FullBookingDto> dtoList = new ArrayList<>();

        switch (state) {
            case "ALL":
                bookings = dao.findBookingsByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = dao.findCurrentBookingsByBooker(userId);
                break;
            case "FUTURE":
                bookings = dao.findFutureBookingsByBooker(userId);
                break;
            case "WAITING":
                bookings = dao.findWaitingBookingsByBooker(userId);
                break;
            case "REJECTED":
                bookings = dao.findRejectedBookingsByBooker(userId);
                break;
            case "PAST":
                bookings = dao.findPastBookingsByBooker(userId);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        for (Booking booking : bookings) {
            dtoList.add(BookingMapper.toFullBookingDto(booking,
                    userService.getUser(booking.getBookerId()),
                    itemService.getSimpleItem(booking.getItemId())));
        }

        return dtoList;
    }

    public List<FullBookingDto> bookingsByOwner(Long userId, String state) {

        if (userService.getUser(userId) == null) {
            throw new WrongParameterException("User не существует");
        }

        List<Booking> bookings;
        List<FullBookingDto> dtoList = new ArrayList<>();

        switch (state) {
            case "ALL":
                bookings = dao.findBookingsByOwner(userId);
                break;
            case "CURRENT":
                bookings = dao.findCurrentBookingsByOwner(userId);
                break;
            case "FUTURE":
                bookings = dao.findFutureBookingsByOwner(userId);
                break;
            case "WAITING":
                bookings = dao.findWaitingBookingsByOwner(userId);
                break;
            case "REJECTED":
                bookings = dao.findRejectedBookingsByOwner(userId);
                break;
            case "PAST":
                bookings = dao.findPastBookingsByOwner(userId);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        for (Booking booking : bookings) {
            dtoList.add(BookingMapper.toFullBookingDto(booking,
                    userService.getUser(booking.getBookerId()),
                    itemService.getSimpleItem(booking.getItemId())));
        }

        return dtoList;
    }

    private void createValidation(Long userId, BookingDto bookingDto) {
        if (userService.getUser(userId) == null) {
            throw new WrongParameterException("User не существует");
        }
        if (bookingDto.getItemId() == null) {
            throw new WrongParameterException("Item не существует");
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Конец не может быть в прошлом");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Конец раньше начала");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало не может быть в прошлом");
        }
        if (!itemService.getSimpleItem(bookingDto.getItemId()).getAvailable()) {
            throw new ValidationException("Item занят");
        }
        if (userId.equals(itemService.getSimpleItem(bookingDto.getItemId()).getOwner())) {
            throw new WrongParameterException("Нельзя делать бронирование у самого себя");
        }
    }
}