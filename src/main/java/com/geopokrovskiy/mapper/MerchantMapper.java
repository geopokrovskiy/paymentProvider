package com.geopokrovskiy.mapper;

import com.geopokrovskiy.dto.MerchantDto;
import com.geopokrovskiy.entity.MerchantEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MerchantMapper {
    @InheritInverseConfiguration
    MerchantDto map(MerchantEntity merchantEntity);
    MerchantEntity map(MerchantDto merchantDto);
}
