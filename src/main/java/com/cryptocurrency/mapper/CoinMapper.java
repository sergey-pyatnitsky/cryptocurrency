package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.Coin;
import com.cryptocurrency.entity.dto.CoinDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CoinMapper {

    @Mapping(source = "coinMarketList", target = "coinMarket")
    CoinDto toDto(Coin coin);

    @Mapping(source = "coinMarket", target = "coinMarketList")
    Coin toModal(CoinDto coinDto);

    List<CoinDto> toDtoList(List<Coin> coinList);

    List<Coin> toModalList(List<CoinDto> coinDtoList);
}
