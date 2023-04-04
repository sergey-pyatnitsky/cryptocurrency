package com.cryptocurrency.entity.dto.markovits;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptimizedPortfolio {
    private double volatility;
    private double meanReturn;
    private double[] weights;
}
