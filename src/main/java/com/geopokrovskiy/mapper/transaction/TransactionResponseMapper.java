package com.geopokrovskiy.mapper.transaction;

import com.geopokrovskiy.dto.transaction.TransactionResponseDto;
import com.geopokrovskiy.entity.TransactionEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionResponseMapper {
    @InheritInverseConfiguration
    TransactionResponseDto map(TransactionEntity entity);
}
