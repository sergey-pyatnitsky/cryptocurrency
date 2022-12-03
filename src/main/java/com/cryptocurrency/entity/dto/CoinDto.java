package com.cryptocurrency.entity.dto;

import lombok.Data;

@Data
public class CoinDto {
    private final String id;
    private final String name;
    private final String symbol;
    private final String image;
    private final String ruDescription;
    private final String enDescription;
    private final CoinMarketDto coinMarket;
}