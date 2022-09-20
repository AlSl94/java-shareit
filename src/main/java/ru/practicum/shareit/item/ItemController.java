package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.utility.Create;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    private final CommentService commentService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        ItemDto item = itemService.create(userId, itemDto);
        log.info("{} created", item.getName());
        return item;
    }

    @PatchMapping(value = "/{itemId}")
    ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                   @PathVariable Long itemId,
                   @RequestBody ItemDto itemDto) {
        ItemDto item = itemService.update(userId, itemId, itemDto);
        log.info("{} updated", item.getName());
        return item;
    }

    @GetMapping(value = "{itemId}")
    ItemInfoDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @PathVariable Long itemId) {
        ItemInfoDto item = itemService.getItem(userId, itemId);
        log.info("Вещь с id {} получена", itemId);
        return item;
    }

    @GetMapping
    List<ItemInfoDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemInfoDto> items = itemService.getUserItems(userId);
        log.info("Были получены все объявления пользователя с id {}", userId);
        return items;
    }

    @GetMapping(value = "/search")
    List<ItemDto> search(@RequestParam String text) {
        List<ItemDto> items = itemService.search(text);
        log.info("Были найдены обьявления по запросу {}", text);
        return items;
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId,
                                  @RequestBody CommentDto commentDto) {
        CommentDto dto = commentService.postComment(userId, itemId, commentDto);
        log.info("Комментарий {} оставлен", dto.getId());
        return dto;
    }
}
