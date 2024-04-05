package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.CustomerEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface CustomerRepository extends R2dbcRepository<CustomerEntity, UUID> {
}
