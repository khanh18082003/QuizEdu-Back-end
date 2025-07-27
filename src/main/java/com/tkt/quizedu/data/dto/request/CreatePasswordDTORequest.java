package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

public record CreatePasswordDTORequest(String password, String confirmPassword)
    implements Serializable {}
