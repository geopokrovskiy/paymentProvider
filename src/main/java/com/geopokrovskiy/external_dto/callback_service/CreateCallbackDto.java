package com.geopokrovskiy.external_dto.callback_service;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCallbackDto {
    private String providerTransactionUid;
    private String type;
    private String provider;
    private String transactionStatus;
}
