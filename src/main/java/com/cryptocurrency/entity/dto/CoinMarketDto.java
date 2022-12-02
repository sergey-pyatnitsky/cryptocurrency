package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoinMarketDto {
    private final Long id;
//    private CoinDto coin;
    private DesignationDto designation;
    private final double currentPrice;
    private final double marketCap;
    private final double fullyDilutedValuation;
    private final double high24h;
    private final double low24h;
    private final double priceChangePercentage24h;
    private final double marketCapChangePercentage24h;
    private final double circulatingSupply;
    private final double totalSupply;
    private final String lastUpdated;
}