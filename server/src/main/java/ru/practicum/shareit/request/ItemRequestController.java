package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto reqDto) {
        ItemRequestDto dto = requestService.create(userId, reqDto);
        log.info("Запрос создан");
        return dto;
    }

    @GetMapping(value = "/{reqId}")
    public ItemRequestDtoWithItems getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long reqId) {
        ItemRequestDtoWithItems dto = requestService.getItemRequestById(userId, reqId);
        log.info("Получен запрос с id {}", reqId);
        return dto;
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequestDtoWithItems> dtos = requestService.getUserItemRequests(userId);
        log.info("Запрос получен");
        return dtos;
    }

    @GetMapping(value = "/all")
    public List<ItemRequestDtoWithItems> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                         @Positive @RequestParam(defaultValue = "10") int size) {
        List<ItemRequestDtoWithItems> dtos = requestService.getAllItemRequests(userId, from, size);
        log.info("Получен список запросов созданных другими пользователями");
        return dtos;
    }
}