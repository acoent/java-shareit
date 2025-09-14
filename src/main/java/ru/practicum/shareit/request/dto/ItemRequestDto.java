package ru.practicum.shareit.request.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "description must be provided")
    private String description;

    private Long requesterId;

    private LocalDateTime created;
}
