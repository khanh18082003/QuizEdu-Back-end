package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tkt.quizedu.data.validator.RoleValid;

import lombok.Builder;
import lombok.With;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
@With
public record UserCreationDTORequest(
    @Email(message = "MESSAGE_INVALID_EMAIL") @NotBlank(message = "MESSAGE_NOT_BLANK") String email,
    @NotBlank(message = "MESSAGE_NOT_BLANK")
        @Size(min = 8, max = 20, message = "MESSAGE_PASSWORD_SIZE")
        String password,
    String firstName,
    String lastName,
    @NotBlank(message = "MESSAGE_NOT_BLANK") String displayName,
    @RoleValid String role)
    implements Serializable {}
