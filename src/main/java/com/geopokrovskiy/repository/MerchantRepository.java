package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.MerchantEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantRepository extends R2dbcRepository<MerchantEntity, UUID> {
    Mono<MerchantEntity> findByUsername(String username);
}
