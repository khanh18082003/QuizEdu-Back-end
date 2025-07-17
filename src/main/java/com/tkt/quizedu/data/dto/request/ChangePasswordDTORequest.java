package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

public record ChangePasswordDTORequest(String email, String newPassword, String confirmPassword)
    implements Serializable {}
