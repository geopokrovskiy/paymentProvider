package com.geopokrovskiy.mapper;

import com.geopokrovskiy.dto.CallbackDto;
import com.geopokrovskiy.entity.CallbackEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CallbackMapper {
    @InheritInverseConfiguration
    CallbackDto map(CallbackEntity entity);

    CallbackEntity map(CallbackDto dto);
}
