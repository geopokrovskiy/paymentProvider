package com.geopokrovskiy.mapper;

import com.geopokrovskiy.dto.CardDto;
import com.geopokrovskiy.entity.CardEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @InheritInverseConfiguration
    CardDto map(CardEntity card);
    CardEntity map(CardDto map);
}
