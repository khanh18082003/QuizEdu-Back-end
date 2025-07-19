package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

public record ForgotPasswordDTORequest(String email) implements Serializable {}
