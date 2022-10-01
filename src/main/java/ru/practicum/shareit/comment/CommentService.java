package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dao.CommentDao;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentDao commentDao;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingDao bookingDao;

    @Transactional
    public CommentDto postComment(Long userId, Long itemId, CommentDto commentDto) {

        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Нельзя оставить пустой комментарий");
        }

        List<Booking> bookings = bookingDao
                .findBookingsByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now());

        if (bookings.stream().noneMatch(b -> Objects.equals(b.getStatus(), BookingStatus.APPROVED))) {
            throw new ValidationException("Нельзя оставить комментарий, без бронирования");
        }

        User user = UserMapper.toUser(userService.getUser(userId));
        Item item = ItemMapper.toItem(itemService.getSimpleItem(itemId));

        Comment comment = CommentMapper.toComment(commentDto, user, item);
        comment.setCreated(LocalDateTime.now());
        commentDao.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

}