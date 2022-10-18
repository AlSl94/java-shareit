package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDtoWithItems {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
