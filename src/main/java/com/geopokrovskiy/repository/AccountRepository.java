package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.AccountEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface AccountRepository extends R2dbcRepository<AccountEntity, UUID> {
}
