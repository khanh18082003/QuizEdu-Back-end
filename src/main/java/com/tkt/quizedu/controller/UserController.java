package com.tkt.quizedu.controller;

import java.util.concurrent.TimeUnit;

import jakarta.validation.Valid;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import com.tkt.quizedu.component.Translator;
import com.tkt.quizedu.data.constant.EndpointConstant;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import com.tkt.quizedu.data.dto.response.SuccessApiResponse;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;
import com.tkt.quizedu.service.user.IUserService;
import com.tkt.quizedu.utils.GenerateVerificationCode;

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
@RequestMapping(EndpointConstant.ENDPOINT_USER)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "USER-CONTROLLER")
@Tag(name = "User Management", description = "APIs for user registration and management")
public class UserController {

  IUserService userService;
  KafkaTemplate<String, String> kafkaTemplate;
  RedisTemplate<String, Object> redisTemplate;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Register a new user",
      description =
          "Creates a new user account and sends a verification email. The verification code is stored in Redis for 10 minutes.")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "User registration details",
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserCreationDTORequest.class),
              examples =
                  @ExampleObject(
                      name = "User Registration Example",
                      summary = "Example of user registration request",
                      value =
                          """
				{
					"firstName": "John",
					"lastName": "Doe",
					"email": "john.doe@example.com",
					"password": "Password123!",
					"role": "STUDENT"
				}
				""")))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
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
						"status": 201,
						"message": "User registered successfully",
						"data": {
							"id": "123e4567-e89b-12d3-a456-426614174000",
							"first_name": "John",
							"last_name": "Doe",
							"email": "john.doe@example.com",
							"display_name": "John Doe",
							"avatar": null,
							"is_active": false,
							"role": "STUDENT",
							"created_at": "2024-01-15T10:30:00Z",
							"updated_at": "2024-01-15T10:30:00Z"
						}
					}
					"""))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            name = "Validation Error",
                            summary = "Invalid input data",
                            value =
                                """
					{
						"code": "M002",
						"status": 400,
						"message": "Validation failed",
						"errors": [
							{
								"field": "email",
								"message": "Email must be valid"
							},
							{
								"field": "password",
								"message": "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
							}
						]
					}
					"""))),
        @ApiResponse(
            responseCode = "409",
            description = "User already exists",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            name = "Conflict Error",
                            summary = "Email already registered",
                            value =
                                """
					{
						"code": "M003",
						"status": 409,
						"message": "Email already exists"
					}
					""")))
      })
  SuccessApiResponse<UserBaseResponse> registerUser(
      @RequestBody @Valid UserCreationDTORequest req) {
    var userResponse = userService.save(req);
    log.info("User with email {} has been registered successfully", req.email());
    // store verification code in Redis
    String code = GenerateVerificationCode.generateCode();
    String key = "user:confirmation:" + userResponse.id();
    redisTemplate.opsForValue().set(key, code, 10, TimeUnit.MINUTES);
    // Send confirmation email via Kafka
    String message =
        String.format(
            "email=%s,name=%s,code=%s", req.email(), req.firstName() + " " + req.lastName(), code);
    kafkaTemplate.send("confirm-account-topic", message);
    log.info("Confirmation email sent to Kafka topic with message: {}", message);
    return SuccessApiResponse.<UserBaseResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.CREATED.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(userResponse)
        .build();
  }
}
