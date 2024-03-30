package com.geopokrovskiy.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.geopokrovskiy.entity.CallbackEntity;
import com.geopokrovskiy.entity.TransactionStatus;
import com.geopokrovskiy.entity.TransactionType;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionDto {
    private UUID uuid;
    private TransactionType transactionType;
    private UUID accountId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID cardId;
    private String language;
    private Double amount;
    private String notificationURL;
    private TransactionStatus transactionStatus;
    private List<CallbackEntity> callbackList;
}
