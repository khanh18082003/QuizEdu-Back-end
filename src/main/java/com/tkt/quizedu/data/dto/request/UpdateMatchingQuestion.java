package com.tkt.quizedu.data.dto.request;

import com.tkt.quizedu.data.constant.MatchingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMatchingQuestion {
    private UUID id;

    private String textContentA;
    private MultipartFile fileContentA;
    private MatchingType typeA;

    private String textContentB;
    private MultipartFile fileContentB;
    private MatchingType typeB;

    private Integer points;
}

