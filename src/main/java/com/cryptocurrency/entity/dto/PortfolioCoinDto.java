package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PortfolioCoinDto {
    private final Long id;
    private final CoinDto coin;
    private final CurrencyValueDto buyPrice;
}