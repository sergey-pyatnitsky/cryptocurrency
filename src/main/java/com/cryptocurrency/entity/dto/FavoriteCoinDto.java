package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FavoriteCoinDto {
    private final Long id;
    private final List<CoinDto> coinList;
    private final UserDto user;
}