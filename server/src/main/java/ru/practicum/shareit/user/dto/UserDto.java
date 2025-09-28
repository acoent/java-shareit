package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "name must be provided")
    @Size(max = 255, message = "Name is too long")
    private String name;

    @NotBlank(message = "email must be provided")
    @Email(message = "email must be valid")
    private String email;
}