package com.geopokrovskiy.mapper.transaction;

import com.geopokrovskiy.dto.transaction.TransactionDto;
import com.geopokrovskiy.entity.TransactionEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionEntity map(TransactionDto dto);
}
