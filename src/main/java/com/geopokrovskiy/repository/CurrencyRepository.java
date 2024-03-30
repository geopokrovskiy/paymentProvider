package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.CurrencyEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CurrencyRepository extends R2dbcRepository<CurrencyEntity, UUID> {
    Mono<CurrencyEntity> findByCurrencyCode(String currencyCode);
}
