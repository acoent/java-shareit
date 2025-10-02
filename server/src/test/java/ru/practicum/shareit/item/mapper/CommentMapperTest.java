package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    void toModel_Success() {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .created(LocalDateTime.now())
                .build();

        Comment comment = commentMapper.toModel(dto);

        assertNotNull(comment);
        assertEquals(dto.getId(), comment.getId());
        assertEquals(dto.getText(), comment.getText());
        assertEquals(dto.getCreated(), comment.getCreated());
    }

    @Test
    void toModel_NullDto_ReturnsNull() {
        Comment comment = commentMapper.toModel(null);
        assertNull(comment);
    }

    @Test
    void toDto_Success() {
        User author = User.builder().id(1L).name("John Doe").build();
        Item item = Item.builder().id(1L).name("Item").build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("Great item!")
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        CommentDto dto = commentMapper.toDto(comment);

        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getCreated(), dto.getCreated());
        assertEquals(author.getId(), dto.getAuthorId());
        assertEquals(author.getName(), dto.getAuthorName());
        assertEquals(item.getId(), dto.getItemId());
    }

    @Test
    void toDto_NullComment_ReturnsNull() {
        CommentDto dto = commentMapper.toDto(null);
        assertNull(dto);
    }
}