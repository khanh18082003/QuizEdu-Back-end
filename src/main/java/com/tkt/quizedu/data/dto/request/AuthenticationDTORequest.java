package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.tkt.quizedu.data.validator.RoleValid;

public record AuthenticationDTORequest(
    @NotBlank(message = "MESSAGE_NOT_BLANK") @Email(message = "MESSAGE_INVALID_EMAIL") String email,
    @NotBlank(message = "MESSAGE_NOT_BLANK") String password,
    @NotBlank(message = "MESSAGE_NOT_BLANK") @RoleValid String role,
    String platform,
    String version,
    String deviceToken)
    implements Serializable {}
