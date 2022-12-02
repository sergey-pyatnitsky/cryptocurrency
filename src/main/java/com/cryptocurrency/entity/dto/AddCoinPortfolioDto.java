package com.cryptocurrency.entity.dto;

import lombok.Data;

@Data
public class AddCoinPortfolioDto {
    private String coinId;
    private String portfolioName;
    private String username;
    private double quantity;
    private double buyPrice;
}
