package ru.practicum.shareit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotBlank(message = "text must be provided")
    private String text;

    private Long authorId;
    private String authorName;
    private Long itemId;
    private LocalDateTime created;
}