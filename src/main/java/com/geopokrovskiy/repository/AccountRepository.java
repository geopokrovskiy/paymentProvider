package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.AccountEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AccountRepository extends R2dbcRepository<AccountEntity, UUID> {
    Flux<AccountEntity> findByMerchantId(UUID merchantId);

    Mono<AccountEntity> findById(UUID uuid);
}
