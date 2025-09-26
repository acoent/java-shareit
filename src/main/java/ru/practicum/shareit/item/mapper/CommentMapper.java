package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

/**
 * Simple DTO <-> Model mapper for Comment.
 * Kept as a Spring component (manual) — mapping logic is trivial.
 */
@Component
public class CommentMapper {

    public Comment toModel(CommentDto dto) {
        if (dto == null) return null;
        Comment c = Comment.builder()
                .id(dto.getId())
                .text(dto.getText())
                .created(dto.getCreated() != null ? dto.getCreated() : LocalDateTime.now())
                .build();
        return c;
    }

    public CommentDto toDto(Comment c) {
        if (c == null) return null;
        CommentDto d = new CommentDto();
        d.setId(c.getId());
        d.setText(c.getText());
        d.setCreated(c.getCreated());
        d.setAuthorId(c.getAuthor() != null ? c.getAuthor().getId() : null);
        d.setAuthorName(c.getAuthor() != null ? c.getAuthor().getName() : null);
        d.setItemId(c.getItem() != null ? c.getItem().getId() : null);
        return d;
    }
}
