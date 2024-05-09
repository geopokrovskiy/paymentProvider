package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.TransactionEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<TransactionEntity, UUID> {
    Mono<TransactionEntity> getTransactionEntityById(UUID id);
}
