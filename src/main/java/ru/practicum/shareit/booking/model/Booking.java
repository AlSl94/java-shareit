package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FutureOrPresent
    @Column(name = "start_date")
    private LocalDateTime start;
    @Future
    @Column(name = "end_date")
    private LocalDateTime end;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    @Column(name = "item_id")
    private Long itemId;
    @Column(name = "booker_id")
    private Long bookerId;
}