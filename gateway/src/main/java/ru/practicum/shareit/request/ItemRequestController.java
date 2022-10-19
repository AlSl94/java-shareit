package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody ItemRequestDto reqDto) {
        log.info("User {} made request {}", userId, reqDto);
        return requestClient.createItemRequest(userId, reqDto);
    }

    @GetMapping(value = "/{reqId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long reqId) {
        log.info("User {}, got request {}", userId, reqId);
        return requestClient.getItemRequestById(userId, reqId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Got item requests of User {}", userId);
        return requestClient.getUserItemRequests(userId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                     @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("User {}, got all requests", userId);
        return requestClient.getAllItemRequests(userId, from, size);
    }
}