package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
	private Long id;
	private Long itemId;
	private Long bookerId;
	private LocalDateTime start;
	private LocalDateTime end;
}
