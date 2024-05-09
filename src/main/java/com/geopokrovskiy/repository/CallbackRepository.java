package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.CallbackEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CallbackRepository extends R2dbcRepository<CallbackEntity, UUID> {
    Flux<CallbackEntity> getCallbackEntitiesByTransactionId(UUID transactionId);

    Mono<CallbackEntity> getCallbackEntityByTransactionId(UUID transactionId);
}
