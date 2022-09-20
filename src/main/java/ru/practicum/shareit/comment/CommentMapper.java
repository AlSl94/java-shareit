package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {

    private CommentMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        return Comment.builder()
                .id(commentDto.getId())
                .author(author)
                .text(commentDto.getText())
                .item(item)
                .created(commentDto.getCreated())
                .build();
    }
}
