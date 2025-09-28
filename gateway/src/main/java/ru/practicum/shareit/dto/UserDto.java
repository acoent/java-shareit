package ru.practicum.shareit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "name must be provided")
    private String name;

    @NotBlank(message = "email must be provided")
    @Email(message = "email must be valid")
    private String email;
}
