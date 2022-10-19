package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto) {
        log.info("User {}, created item {}", userId, itemDto.getName());
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("User {} updated item {}", userId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId) {
        log.info("User {}, got item {}", userId, itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Got all items of user {}", userId);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                              @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Found items with query {}", text);
        return itemClient.searchItem(text, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody CommentDto commentDto) {
        log.info("User {} left comment {}", userId, commentDto);
        return itemClient.postComment(userId, itemId, commentDto);
    }
}
