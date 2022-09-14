package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        ItemDto item = itemService.create(userId, itemDto);
        log.info("Вещь {} была создана c id {}", item.getName(), item.getId());
        return item;
    }

    @PatchMapping(value = "/{itemId}")
    ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                   @PathVariable Long itemId,
                   @RequestBody ItemDto itemDto) {
        ItemDto item = itemService.update(userId, itemId, itemDto);
        log.info("Вещь {} c id {} была обновлена", item.getName(), item.getId());
        return item;
    }

    @GetMapping(value = "{itemId}")
    ItemDto getItem(@PathVariable Long itemId) {
        ItemDto item = itemService.getItem(itemId);
        log.info("Вещь с id {} получена", itemId);
        return item;
    }
    @GetMapping
    List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> items = itemService.getUserItems(userId);
        log.info("Были получены все объявления пользователя с id {}", userId);
        return items;
    }
    @GetMapping(value = "/search")
    List<ItemDto> search(@RequestParam String text) {
        List<ItemDto> items = itemService.search(text);
        log.info("Были найдены обьявления по запросу {}", text);
        return items;
    }

}
