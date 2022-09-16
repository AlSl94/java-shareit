package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserService userService;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        userService.getUser(userId);
        return itemDao.create(userId, itemDto);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        ItemDto updatedItem = getItem(itemId);
        if (itemDto.getName() != null) updatedItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) updatedItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) updatedItem.setAvailable(itemDto.getAvailable());
        return itemDao.update(userId, itemId, updatedItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return itemDao.getItem(itemId);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemDao.getUserItems(userId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemDao.search(text);
    }
}