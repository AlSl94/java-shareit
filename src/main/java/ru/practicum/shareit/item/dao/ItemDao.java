package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemDao {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItem(Long itemId);

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> search(String text);

}
