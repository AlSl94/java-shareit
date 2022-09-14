package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull(groups = Create.class)
    private String name;
    @NotNull(groups = Create.class)
    @NotBlank(groups = Create.class)
    @Email(groups = Create.class)
    private String email;
}
