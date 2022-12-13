package com.cryptocurrency.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioDto {
    private Long id;
    private String name;
    private UserDto userDto;
    private List<PortfolioCoinDto> portfolioCoins;
}