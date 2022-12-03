package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoinMarketDto {
    private final Long id;
    private final CurrencyValueDto currentPrice;
    private final CurrencyValueDto marketCap;
    private final CurrencyValueDto fullyDilutedValuation;
    private final CurrencyValueDto high24h;
    private final CurrencyValueDto low24h;
    private final CurrencyValueDto priceChangePercentage24h;
    private final CurrencyValueDto marketCapChangePercentage24h;
    private final CurrencyValueDto circulatingSupply;
    private final CurrencyValueDto totalSupply;
    private final String lastUpdated;
}