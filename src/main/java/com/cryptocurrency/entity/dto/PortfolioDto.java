package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PortfolioDto {
    private final Long id;
    private final String name;
    private final List<PortfolioCoinDto> portfolioCoins;
}