package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.CoinMarket;
import com.cryptocurrency.entity.dto.CoinMarketDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = CurrencyValueMapper.class)
public interface CoinMarketMapper {

    CoinMarketDto toDto(CoinMarket coin);

    @Mapping(ignore = true, target = "coin")
    CoinMarket toModal(CoinMarketDto coinMarketDto);

    List<CoinMarketDto> toDtoList(List<CoinMarket> coinMarketList);

    List<CoinMarket> toModalList(List<CoinMarketDto> coinMarketDtoList);
}
