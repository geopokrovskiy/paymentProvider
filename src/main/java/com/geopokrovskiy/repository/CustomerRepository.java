package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.CustomerEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerRepository extends R2dbcRepository<CustomerEntity, UUID> {
    Mono<CustomerEntity> findByUserName(String userName);
}
