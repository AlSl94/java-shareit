package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}