package com.geopokrovskiy.mapper;

import com.geopokrovskiy.dto.TransactionDto;
import com.geopokrovskiy.entity.TransactionEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @InheritInverseConfiguration
    TransactionDto map(TransactionEntity entity);

    TransactionEntity map(TransactionDto dto);
}
