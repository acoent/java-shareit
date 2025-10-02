package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 10, 15, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 15, 12, 0, 0);

        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"itemId\":1,\"start\":\"2023-10-15T10:00:00\",\"end\":\"2023-10-15T12:00:00\"}";

        BookingDto result = json.parse(content).getObject();

        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo("2023-10-15T10:00:00");
        assertThat(result.getEnd()).isEqualTo("2023-10-15T12:00:00");
    }
}
