package com.geopokrovskiy.mapper;

import com.geopokrovskiy.dto.AccountDto;
import com.geopokrovskiy.entity.AccountEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @InheritInverseConfiguration
    AccountDto map(AccountEntity account);
    AccountEntity map(AccountDto dto);
}
