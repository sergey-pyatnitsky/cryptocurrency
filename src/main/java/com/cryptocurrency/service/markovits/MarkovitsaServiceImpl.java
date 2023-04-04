package com.cryptocurrency.service.markovits;

import com.cryptocurrency.entity.dto.gesko.OhlcDto;
import com.cryptocurrency.entity.dto.markovits.MarkovitsDto;
import com.cryptocurrency.entity.dto.markovits.OptimizedPortfolio;
import com.cryptocurrency.entity.dto.markovits.PartsOfStock;
import com.cryptocurrency.service.finance.portfolio.MarkowitzModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.ojalgo.function.constant.BigMath;
import org.ojalgo.matrix.Primitive64Matrix;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class MarkovitsaServiceImpl implements MarkovitsaService {

    //Ковариационная матрица
    private double[][] getCovarianceMatrix(double[][] changeDayPrices) {
        return new Covariance(
                MatrixUtils.createRealMatrix(changeDayPrices)
        ).getCovarianceMatrix().getData();
    }

    //относительные изменения ко дню в %
    private double[][] getChangesToPreviousDay(Map<String, List<OhlcDto>> coinPriceMap) {
        int PRICE_LIST_SIZE = 94;
        double[][] changeDayPrices = new double[PRICE_LIST_SIZE][coinPriceMap.keySet().size()];
        String[] coinsId = coinPriceMap.keySet().toArray(new String[0]);

        for (int i = 0; i < (PRICE_LIST_SIZE - 1); i++) {
            for (int j = 0; j < coinPriceMap.keySet().size(); j++) {
                List<OhlcDto> ohlcList = coinPriceMap.get(coinsId[j]);
                changeDayPrices[i][j] = (ohlcList.get(i + 1).getClose() - ohlcList.get(i).getClose()) /
                        ohlcList.get(i).getClose() * 100;
            }
        }

        return changeDayPrices;
    }

    //Ожидаемая доходность в %
    private double[] getProfitability(double[][] changeDayPrices) {
        double[] profitability = new double[changeDayPrices[0].length];

        for (int column = 0; column < changeDayPrices[0].length; column++) {
            double sumPrices = 0;
            for (double[] changeDayPrice : changeDayPrices) {
                sumPrices += changeDayPrice[column];
            }
            profitability[column] = sumPrices / changeDayPrices.length;
        }

        return profitability;
    }

    //Риски в %
    private double[] getDeviation(double[][] changeDayPrices) {
        double[] deviation = new double[changeDayPrices[0].length];

        for (int column = 0; column < changeDayPrices[0].length; column++) {
            double[] columnData = new double[changeDayPrices.length];
            for (int row = 0; row < changeDayPrices.length; row++) {
                columnData[row] = changeDayPrices[row][column];
            }

            StandardDeviation sd = new StandardDeviation(false);
            deviation[column] = sd.evaluate(columnData);
        }

        return deviation;
    }

    //Общий риск инвестиционного портфеля в % (на основе доли акций в портфеле)
    private double getTotalRiskOfPortfolio(double[] partsOfStock, double[][] covarianceMatrix) {
        RealMatrix covMatrix = new Array2DRowRealMatrix(covarianceMatrix);
        RealMatrix profMatrix = new Array2DRowRealMatrix(partsOfStock);
        RealMatrix transposeProfMatrix = profMatrix.transpose();

        return Math.sqrt(transposeProfMatrix.multiply(covMatrix.multiply(profMatrix)).getData()[0][0]);
    }

    //Общая доходность инвестиционного портфеля в %
    private double getTotalProfitabilityOfPortfolio(double[] partsOfStock, double[] profitability) {
        double totalProfitability = 0;

        for (int column = 0; column < partsOfStock.length; column++) {
            totalProfitability += profitability[column] * partsOfStock[column];
        }

        return totalProfitability;
    }

    private double[] mapListToArrayStocks(List<PartsOfStock> partsOfStockList) {
        double[] partStockArray = new double[partsOfStockList.size()];
        for (int i = 0; i < partsOfStockList.size(); i++) {
            partStockArray[i] = partsOfStockList.get(i).getValue();
        }
        return partStockArray;
    }

    /**
     * анализ текущего портфеля
     * <p>
     * расчёт дневного изменения (это всё в процентах)
     * ожидаемая доходность
     * риск
     * <p>
     * ковариация
     * ковариаобщий риск портфеляция
     */
    @Override
    public MarkovitsDto getMarkovitsAnalysis(Map<String, List<OhlcDto>> coinPriceMap, List<PartsOfStock> partsOfStockList) {
        double[][] changesToPreviousDay = this.getChangesToPreviousDay(coinPriceMap);

        double[] profitability = this.getProfitability(changesToPreviousDay);
        double[] deviation = this.getDeviation(changesToPreviousDay);

        double[][] covarianceMatrix = this.getCovarianceMatrix(changesToPreviousDay);

        double totalRiskOfPortfolio = this.getTotalRiskOfPortfolio(mapListToArrayStocks(partsOfStockList), covarianceMatrix);
        double totalProfitabilityOfPortfolio = this.getTotalProfitabilityOfPortfolio(mapListToArrayStocks(partsOfStockList), profitability);

        return MarkovitsDto.builder()
                .profitability(profitability)
                .deviation(deviation)
                .optimizedPortfolio(this.getOptimisation(covarianceMatrix, profitability))
                .covarianceMatrix(covarianceMatrix)
                .totalRiskOfPortfolio(totalRiskOfPortfolio)
                .totalProfitabilityOfPortfolio(totalProfitabilityOfPortfolio).build();
    }

    private OptimizedPortfolio getOptimisation(double[][] covarianceMatrix, double[] profitability) {
        final Primitive64Matrix tmpCovariances = Primitive64Matrix.FACTORY
                .rows(covarianceMatrix);
        /*
          Excess Returns (Избыточная доходность) - Рентабельность актива минус
          безрисковая доходность. Или насколько больше доходность актива
          в отличие от безрискового актива.

          тут видимо нужно указывать ожидаемые жоходности каждой акции
         */
        final Primitive64Matrix.DenseReceiver tmpExcessReturnsBuilder =
                Primitive64Matrix.FACTORY.makeDense(profitability.length, 1);

        for (int i = 0; i < profitability.length; i++) {
            tmpExcessReturnsBuilder.set(i, 0, profitability[i]);
        }

        final Primitive64Matrix tmpExcessReturns = tmpExcessReturnsBuilder.build();

        final MarkowitzModel tmpMarkowitzModel = new MarkowitzModel(tmpCovariances, tmpExcessReturns);

        for (int i = 0; i < covarianceMatrix.length; i++) {
            tmpMarkowitzModel.setLowerLimit(i, BigMath.ZERO);
            tmpMarkowitzModel.setUpperLimit(i, BigMath.ONE);
        }

        tmpMarkowitzModel.setShortingAllowed(true);

        tmpMarkowitzModel.normalise();
        return OptimizedPortfolio.builder()
                .volatility(tmpMarkowitzModel.getVolatility())
                .meanReturn(tmpMarkowitzModel.getMeanReturn())
                .weights(tmpMarkowitzModel.getWeights().stream().mapToDouble(BigDecimal::doubleValue).toArray()).build();

    }
}
