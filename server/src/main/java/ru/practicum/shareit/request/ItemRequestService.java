package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utility.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestDao requestDao;
    private final ItemDao itemDao;
    private final UserService userService;

    public ItemRequestDto create(Long userId, ItemRequestDto requestDto) {
        if (requestDto.getDescription() == null) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (userService.getUser(userId) == null) {
            throw new WrongParameterException("Пользователя не существует");
        }
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto);
        request.setRequestorId(userId);
        request.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(requestDao.save(request));
    }

    public ItemRequestDtoWithItems getItemRequestById(Long userId, Long reqId) {
        if (userService.getUser(userId) == null) {
            throw new WrongParameterException("Пользователя не существует");
        }
        ItemRequest request = requestDao.findById(reqId)
                .orElseThrow(() -> new WrongParameterException("ItemRequest не сущетвует"));

        ItemRequestDtoWithItems dto = ItemRequestMapper.toItemRequestDtoWithItems(request);
        dto.setItems(ItemMapper.toDtoList(itemDao.findAllByRequestId(request.getId())));

        return dto;
    }


    public List<ItemRequestDtoWithItems> getUserItemRequests(Long userId) {

        if (userService.getUser(userId) == null) {
            throw new WrongParameterException("Пользователя не существует");
        }

        List<ItemRequestDtoWithItems> requestsDto = new ArrayList<>();

        requestDao.findAllByRequestorIdOrderByCreatedDesc(userId)
                .forEach(request -> {
                    ItemRequestDtoWithItems dto = ItemRequestMapper.toItemRequestDtoWithItems(request);
                    dto.setItems(ItemMapper.toDtoList(itemDao.findAllByRequestId(request.getId())));
                    requestsDto.add(dto);
                });

        return requestsDto;
    }


    public List<ItemRequestDtoWithItems> getAllItemRequests(Long userId, int from, int size) {

        if (userService.getUser(userId) == null) {
            throw new WrongParameterException("Пользователя не существует");
        }

        if (from < 0 || size < 0) {
            throw new WrongParameterException("Пользователя не существует");
        }

        Pageable pageable = FromSizeRequest.of(from, size);

        List<ItemRequest> requests = requestDao.findAll(pageable)
                .stream()
                .filter(itemRequest -> !itemRequest.getRequestorId().equals(userId))
                .collect(Collectors.toList());

        List<ItemRequestDtoWithItems> requestsDto = new ArrayList<>();

        requests.forEach(request -> {
            ItemRequestDtoWithItems dto = ItemRequestMapper.toItemRequestDtoWithItems(request);
            dto.setItems(ItemMapper.toDtoList(itemDao.findAllByRequestId(request.getId())));
            requestsDto.add(dto);
        });

        return requestsDto;
    }
}
