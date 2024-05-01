package com.geopokrovskiy.mapper;

import com.geopokrovskiy.dto.CustomerDto;
import com.geopokrovskiy.entity.CustomerEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @InheritInverseConfiguration
    CustomerDto map(CustomerEntity entity);

    CustomerEntity map(CustomerDto map);

}
