package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.CardEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CardRepository extends R2dbcRepository<CardEntity, UUID> {
    Mono<CardEntity> findByCardNumber(String cardNumber);
}
