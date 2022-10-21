package ru.practicum.shareit.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class CommentDto {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}