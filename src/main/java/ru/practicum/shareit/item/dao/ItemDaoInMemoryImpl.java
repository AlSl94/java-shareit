package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ItemDaoInMemoryImpl implements ItemDao {

    Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(++id);
        item.setOwner(userId);
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (!Objects.equals(userId, items.get(itemId).getOwner())) {
            throw new WrongParameterException("Редактировать может только пользователь, которому принадлежит вещь");
        }
        ItemDto updatedItem = getItem(itemId);
        if (itemDto.getName() != null) updatedItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) updatedItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) updatedItem.setAvailable(itemDto.getAvailable());
        Item item = ItemMapper.toItem(updatedItem);
        items.put(item.getId(), item);
        return updatedItem;
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return items.values().stream()
                .filter(i -> i.getOwner().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        String loweredText = text.toLowerCase();
        return items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(loweredText)
                        || i.getDescription().toLowerCase().contains(loweredText))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
