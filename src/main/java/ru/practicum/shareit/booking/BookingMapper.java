package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.FullBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;


public class BookingMapper {

    private BookingMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static BookingDto toBookingDto(Booking booking) {

        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItemId())
                .bookerId(booking.getBookerId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }


    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .itemId(bookingDto.getItemId())
                .bookerId(bookingDto.getBookerId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public static FullBookingDto toFullBookingDto(Booking booking, UserDto booker, ItemDto item) {

        return FullBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booker)
                .item(item)
                .build();
    }

}
