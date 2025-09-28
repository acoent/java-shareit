package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {

    private Long id;

    @NotBlank(message = "name must be provided")
    private String name;

    @NotBlank(message = "description must be provided")
    private String description;

    @NotNull(message = "available must be provided")
    private Boolean available;

    private Long requestId;

    private Long ownerId;
}
