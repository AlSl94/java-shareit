package ru.practicum.shareit.request.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDto {

    private Long id;
    private String description;
    private LocalDateTime created;
}
