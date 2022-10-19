package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingStatus {
	// Все
	ALL,
	// Текущие
	CURRENT,
	// Будущие
	FUTURE,
	// Подтвержденные
	APPROVED,
	// Завершенные
	PAST,
	// Отклоненные
	REJECTED,
	// Ожидающие подтверждения
	WAITING;

	public static Optional<BookingStatus> from(String stringState) {
		for (BookingStatus status : values()) {
			if (status.name().equalsIgnoreCase(stringState)) {
				return Optional.of(status);
			}
		}
		return Optional.empty();
	}
}
