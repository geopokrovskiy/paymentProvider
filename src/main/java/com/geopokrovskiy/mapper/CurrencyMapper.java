package com.geopokrovskiy.mapper;

import com.geopokrovskiy.dto.CurrencyDto;
import com.geopokrovskiy.entity.CurrencyEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    @InheritInverseConfiguration
    CurrencyDto map(CurrencyEntity currency);
    CurrencyEntity map(CurrencyDto dto);

}
