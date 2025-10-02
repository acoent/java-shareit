package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.of(2023, 10, 15, 10, 0, 0);

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(1L)
                .created(created)
                .items(Collections.emptyList())
                .build();

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"description\":\"Need a drill\",\"requesterId\":1,\"created\":\"2023-10-15T10:00:00\",\"items\":[]}";

        ItemRequestDto result = json.parse(content).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getRequesterId()).isEqualTo(1L);
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 15, 10, 0, 0));
        assertThat(result.getItems()).isEmpty();
    }
}
