package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dao.CommentDao;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utility.FromSizeRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemDao itemDao;
    private final BookingDao bookingDao;
    private final CommentDao commentDao;
    private final UserService userService;


    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        createValidation(userId, itemDto);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        itemDao.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(getSimpleItem(itemId));

        if (!Objects.equals(item.getOwner(), userId)) {
            throw new WrongParameterException("Редактировать может только владелец");
        }

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        itemDao.save(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemInfoDto getItem(Long userId, Long itemId) {

        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new WrongParameterException("Item не сущетвует"));

        List<Booking> lastBookings = bookingDao.findLastBookingByItemId(userId, itemId);
        List<Booking> nextBookings = bookingDao.findNextBookingsByItemIdAndUserId(userId, itemId);
        BookingDto nextBookingDto = null;
        BookingDto lastBookingDto = null;
        try {
            nextBookingDto = BookingMapper.toBookingDto(nextBookings.get(0));
            lastBookingDto = BookingMapper.toBookingDto(lastBookings.get(0));
        } catch (IndexOutOfBoundsException ignored) {

        }

        List<Comment> comments = commentDao.findAllByItemId(itemId);
        List<CommentDto> dtos = new ArrayList<>();
        if (!comments.isEmpty()) {
            for (Comment comment : comments) {
                dtos.add(CommentMapper.toCommentDto(comment));
            }
        }
        return ItemMapper.toItemInfoDto(item, nextBookingDto, lastBookingDto, dtos);
    }

    public List<ItemInfoDto> getUserItems(Long userId, int from, int size) {

        Pageable pageable = FromSizeRequest.of(from, size);

        return itemDao.findItemsByOwner(userId, pageable).stream()
                .map(item -> getItem(userId, item.getId()))
                .collect(Collectors.toList());
    }

    public List<ItemDto> search(String text, int from, int size) {

        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        Pageable pageable = FromSizeRequest.of(from, size);
        return ItemMapper.toDtoList(itemDao.findItemsByText(text, pageable));
    }

    public ItemDto getSimpleItem(Long itemId) {
        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new WrongParameterException("Item не сущетвует"));
        return ItemMapper.toItemDto(item);
    }

    private void createValidation(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("Не указан пользователь");
        }
        if (userService.getUser(userId) == null) {
            throw new WrongParameterException("Пользователя не существует");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Нужно указать available");
        }
        if (itemDto.getName() == null) {
            throw new ValidationException("Нужно указать name");
        }
        if (itemDto.getDescription() == null) {
            throw new ValidationException("Нужно указать description");
        }
    }
}