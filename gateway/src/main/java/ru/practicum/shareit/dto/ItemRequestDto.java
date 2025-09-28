package ru.practicum.shareit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    private List<ItemDto> items;
}
