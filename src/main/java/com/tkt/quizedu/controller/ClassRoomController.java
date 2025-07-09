package com.tkt.quizedu.controller;

import com.tkt.quizedu.data.constant.EndpointConstant;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_CLASSROOM)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "CLASSROOM-CONTROLLER")
@Tag(name = "ClassRoom Management", description = "APIs for classRoom registration and management")
public class ClassRoomController {

}
