package com.geopokrovskiy.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.geopokrovskiy.entity.TransactionEntity;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccountDto {
    private UUID uuid;
    private String currencyCode;
    private UUID merchantId;
    private Long balance;
}
