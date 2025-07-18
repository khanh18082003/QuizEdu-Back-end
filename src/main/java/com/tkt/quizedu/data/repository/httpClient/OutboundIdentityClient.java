package com.tkt.quizedu.data.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import com.tkt.quizedu.data.dto.request.ExchangeTokenRequest;
import com.tkt.quizedu.data.dto.response.ExchangeTokenResponse;

import feign.QueryMap;

@FeignClient(name = "outbound-identity-client", url = "${outbound.google.identity.url}")
public interface OutboundIdentityClient {
  @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);
}
