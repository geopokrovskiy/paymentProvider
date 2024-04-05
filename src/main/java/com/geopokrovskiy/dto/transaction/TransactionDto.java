package com.geopokrovskiy.dto.transaction;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.geopokrovskiy.dto.CardDto;
import com.geopokrovskiy.dto.CustomerDto;
import com.geopokrovskiy.entity.TransactionStatus;
import com.geopokrovskiy.entity.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionDto {
    private UUID uuid;
    private TransactionType transactionType;
    private UUID accountId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CardDto card;
    private CustomerDto customer;
    private String language;
    private Double amount;
    private String notificationURL;
    private TransactionStatus transactionStatus;
}
