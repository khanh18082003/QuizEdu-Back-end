package com.tkt.quizedu.data.dto.response;

import lombok.Builder;

@Builder
public record AuthenticationResponse(String accessToken, String role) {}
