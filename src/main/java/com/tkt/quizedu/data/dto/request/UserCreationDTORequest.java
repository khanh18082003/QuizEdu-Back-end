package com.tkt.quizedu.data.dto.request;

import com.tkt.quizedu.data.validator.RoleValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.With;

import java.io.Serializable;

@Builder
@With
public record UserCreationDTORequest(
    @Email(message = "MESSAGE_INVALID_EMAIL") @NotBlank(message = "MESSAGE_NOT_BLANK") String email,
    @NotBlank(message = "MESSAGE_NOT_BLANK")
        @Size(min = 8, max = 20, message = "MESSAGE_PASSWORD_SIZE")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$",
            message = "MESSAGE_INVALID_FORMAT_PASSWORD")
        String password,
    String firstName,
    String lastName,
    @NotBlank(message = "MESSAGE_NOT_BLANK") String displayName,
    @RoleValid String role)
    implements Serializable {}
