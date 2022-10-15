package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.FullBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    ObjectMapper mapper = new ObjectMapper();
    private Booking booking;
    private User booker;
    private Item item;

    @BeforeEach
    void beforeEach() {
        mapper.registerModule(new JavaTimeModule());
        User owner = new User(1L, "user1", "user1@mail.ru");
        booker = new User(2L, "user2", "user2@mail.ru");
        item = new Item(1L, "item", "description",
                true, owner.getId(), null);
        booking = new Booking(1L, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING, item.getId(), booker.getId());
    }

    @Test
    void createBookingTest() throws Exception {
        UserDto bookerDto = UserMapper.toUserDto(booker);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        BookingDto simpleDto = BookingMapper.toBookingDto(booking);
        FullBookingDto fullDto = BookingMapper.toFullBookingDto(booking, bookerDto, itemDto);

        when(bookingService.create(any(), any())).thenReturn(fullDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(simpleDto))
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fullDto.getId()))
                .andExpect(jsonPath("$.booker.id").value(fullDto.getBooker().getId()));
    }

    @Test
    void updateBookingStatusTest() throws Exception {
        UserDto bookerDto = UserMapper.toUserDto(booker);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        FullBookingDto fullDto = BookingMapper.toFullBookingDto(booking, bookerDto, itemDto);
        fullDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateStatus(1L, 1L, true)).thenReturn(fullDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingTest() throws Exception {
        UserDto bookerDto = UserMapper.toUserDto(booker);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        FullBookingDto fullDto = BookingMapper.toFullBookingDto(booking, bookerDto, itemDto);
        when(bookingService.getBooking(1L, 1L)).thenReturn(fullDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fullDto.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void bookingsByBookerTest() throws Exception {
        UserDto bookerDto = UserMapper.toUserDto(booker);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        FullBookingDto fullDto = BookingMapper.toFullBookingDto(booking, bookerDto, itemDto);
        when(bookingService.bookingsByBooker(2L, "ALL", 0, 10))
                .thenReturn(Collections.singletonList(fullDto));

        mockMvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(fullDto.getId()))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void bookingsByOwnerTest() throws Exception {
        UserDto bookerDto = UserMapper.toUserDto(booker);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        FullBookingDto fullDto = BookingMapper.toFullBookingDto(booking, bookerDto, itemDto);
        when(bookingService.bookingsByBooker(1L, "ALL", 0, 10))
                .thenReturn(Collections.singletonList(fullDto));

        mockMvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(fullDto.getId()))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }
}