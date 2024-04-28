package com.geopokrovskiy.mapper.merchant;

import com.geopokrovskiy.dto.merchant.MerchantDto;
import com.geopokrovskiy.entity.MerchantEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MerchantMapper {
    MerchantEntity map(MerchantDto merchantDto);
}
