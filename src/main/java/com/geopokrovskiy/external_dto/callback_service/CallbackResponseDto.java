package com.geopokrovskiy.external_dto.callback_service;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CallbackResponseDto {
    private UUID providerTransactionUid;
    private String type;
    private String provider;
    private UUID uid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

