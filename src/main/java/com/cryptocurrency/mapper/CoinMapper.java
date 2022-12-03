package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.Coin;
import com.cryptocurrency.entity.dto.CoinDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = CoinMarketMapper.class)
public interface CoinMapper {

    @Mapping(ignore = true, target = "coinMarket")
    CoinDto toDto(Coin coin);

    @Mapping(ignore = true, target = "coinMarketList")
    Coin toModal(CoinDto coinDto);

    List<CoinDto> toDtoList(List<Coin> coinList);

    List<Coin> toModalList(List<CoinDto> coinDtoList);
}
