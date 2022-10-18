package ru.practicum.shareit.item.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long requestId;
}