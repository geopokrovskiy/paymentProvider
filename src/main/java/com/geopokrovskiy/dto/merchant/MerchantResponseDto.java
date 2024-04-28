package com.geopokrovskiy.dto.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MerchantResponseDto {
    private UUID id;
    private String username;
    private LocalDateTime registrationDate;
    private String country;
}
