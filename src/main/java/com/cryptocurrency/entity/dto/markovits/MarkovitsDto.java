package com.cryptocurrency.entity.dto.markovits;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarkovitsDto {
    private double[] profitability;
    private double[] deviation;
    private double[][] covarianceMatrix;
    private double totalRiskOfPortfolio;
    private double totalProfitabilityOfPortfolio;
}
