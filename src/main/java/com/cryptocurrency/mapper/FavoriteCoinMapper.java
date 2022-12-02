package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.FavoriteCoin;
import com.cryptocurrency.entity.dto.FavoriteCoinDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CoinMapper.class})
public interface FavoriteCoinMapper {

    FavoriteCoinDto toDto(FavoriteCoin favoriteCoin);

    FavoriteCoin toModal(FavoriteCoinDto favoriteCoinDto);

    List<FavoriteCoinDto> toDtoList(List<FavoriteCoin> favoriteCoinList);

    List<FavoriteCoin> toModalList(List<FavoriteCoinDto> favoriteCoinDtoList);
}
