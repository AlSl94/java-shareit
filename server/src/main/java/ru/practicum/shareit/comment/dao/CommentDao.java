package ru.practicum.shareit.comment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentDao extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(Long itemId);
}
