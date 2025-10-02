package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.of(2023, 10, 15, 10, 0, 0);

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .authorId(1L)
                .authorName("John Doe")
                .itemId(1L)
                .created(created)
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
        assertThat(result).extractingJsonPathNumberValue("$.authorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"text\":\"Great item!\",\"authorId\":1,\"authorName\":\"John Doe\",\"itemId\":1,\"created\":\"2023-10-15T10:00:00\"}";

        CommentDto result = json.parse(content).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Great item!");
        assertThat(result.getAuthorId()).isEqualTo(1L);
        assertThat(result.getAuthorName()).isEqualTo("John Doe");
        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getCreated()).isEqualTo("2023-10-15T10:00:00");
    }
}
