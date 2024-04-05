package com.geopokrovskiy.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.geopokrovskiy.entity.TransactionStatus;
import lombok.Data;

import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionResponseDto {
    private UUID uuid;
    private TransactionStatus transactionStatus;

    @JsonProperty("transaction_id")
    public UUID getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
