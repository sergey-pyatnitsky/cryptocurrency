package com.cryptocurrency.mapper.gesko;

import com.cryptocurrency.entity.dto.CoinMarketDto;
import com.cryptocurrency.entity.dto.gesko.GeskoCoinMarketDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GeskoMapper {

    @Mapping(ignore = true, target = "id")
    CoinMarketDto toBaseDto(GeskoCoinMarketDto coinMarketDto);

    List<CoinMarketDto> toBaseDtoList(List<GeskoCoinMarketDto> coinMarketDtoList);
}
