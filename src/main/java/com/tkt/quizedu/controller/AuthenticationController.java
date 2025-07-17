package com.tkt.quizedu.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tkt.quizedu.component.Translator;
import com.tkt.quizedu.data.constant.EndpointConstant;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.request.*;
import com.tkt.quizedu.data.dto.response.AuthenticationResponse;
import com.tkt.quizedu.data.dto.response.SuccessApiResponse;
import com.tkt.quizedu.service.auth.IAuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_AUTHENTICATION)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
@Tag(
    name = "Authentication Management",
    description = "APIs for authentication and authorization management")
public class AuthenticationController {

  IAuthenticationService authenticationService;

  @PostMapping("/verification-code")
  @Operation(
      summary = "Validate Verification Code",
      description = "Validates the verification code for user registration or password reset.")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Verification code request details",
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = VerificationCodeDTORequest.class),
              examples =
                  @ExampleObject(
                      name = "Verification Code Example",
                      summary = "Example of verification code request",
                      value =
                          """
				{
					"user_id": "686d33f18895cd00e65bb25a",
					"code": "Y05T6V",
				}
				""")))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Verification code validated successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SuccessApiResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Success Response",
                            summary = "Successful user registration",
                            value =
                                """
					{
						"code": "M000",
						"status": 200,
						"message": "Success",
					}
					"""))),
      })
  SuccessApiResponse<Void> validateVerificationCode(
      @Valid @RequestBody VerificationCodeDTORequest req) {
    authenticationService.validateVerificationCode(req.email(), req.code());

    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(ErrorCode.MESSAGE_SUCCESS.getStatusCode().value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }

  @PostMapping
  SuccessApiResponse<AuthenticationResponse> authenticate(
      @Valid @RequestBody AuthenticationDTORequest req, HttpServletResponse httpServletResponse) {
    AuthenticationResponse response = authenticationService.authenticate(req, httpServletResponse);

    return SuccessApiResponse.<AuthenticationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(ErrorCode.MESSAGE_SUCCESS.getStatusCode().value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @PostMapping("/refresh-token")
  SuccessApiResponse<AuthenticationResponse> refreshToken(HttpServletRequest httpServletRequest) {
    log.info("Refreshing token for request: {}", httpServletRequest.getRequestURI());
    AuthenticationResponse response = authenticationService.refreshToken(httpServletRequest);
    log.info("Token refreshed successfully: {}", response);
    return SuccessApiResponse.<AuthenticationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(ErrorCode.MESSAGE_SUCCESS.getStatusCode().value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @PostMapping("/logout")
  SuccessApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
    authenticationService.logout(request, response);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(ErrorCode.MESSAGE_SUCCESS.getStatusCode().value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }

  @PostMapping("/resend-code")
  SuccessApiResponse<Void> resendVerificationCode(@Valid @RequestBody ResendCodeDTORequest req) {
    authenticationService.resendVerificationCode(req);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(ErrorCode.MESSAGE_SUCCESS.getStatusCode().value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }

  @PostMapping("/forgot-password")
  SuccessApiResponse<Void> verifyEmail(@Valid @RequestBody ForgotPasswordDTORequest req) {
    authenticationService.verifyEmail(req);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(ErrorCode.MESSAGE_SUCCESS.getStatusCode().value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }
}
