package com.geopokrovskiy.mapper.merchant;

import com.geopokrovskiy.dto.merchant.MerchantResponseDto;
import com.geopokrovskiy.entity.MerchantEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MerchantResponseMapper {
    @InheritInverseConfiguration
    MerchantResponseDto map(MerchantEntity merchantEntity);
}
