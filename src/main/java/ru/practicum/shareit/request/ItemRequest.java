package ru.practicum.shareit.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;
    private LocalDateTime created;
}
