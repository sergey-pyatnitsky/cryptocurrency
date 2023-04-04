package com.cryptocurrency.service.finance.portfolio;

import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.function.special.ErrorFunction;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.type.StandardType;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

import java.math.BigDecimal;
import java.util.List;

import static org.ojalgo.function.constant.PrimitiveMath.*;

/**
 * A FinancePortfolio is primarily a set of portfolio asset weights.
 */
public abstract class FinancePortfolio implements Comparable<FinancePortfolio> {

    public interface Context {

        double calculatePortfolioReturn(final FinancePortfolio weightsPortfolio);

        double calculatePortfolioVariance(final FinancePortfolio weightsPortfolio);

        Primitive64Matrix getAssetReturns();

        Primitive64Matrix getAssetVolatilities();

        Primitive64Matrix getCorrelations();

        Primitive64Matrix getCovariances();

        int size();

    }

    protected static final Primitive64Matrix.Factory MATRIX_FACTORY = Primitive64Matrix.FACTORY;

    protected FinancePortfolio() {
        super();
    }

    public final int compareTo(final FinancePortfolio reference) {
        return NumberContext.compare(this.getSharpeRatio(), reference.getSharpeRatio());
    }



    /**
     * The mean/expected return of this instrument. May return either the absolute or excess return of the
     * instrument. The context in which an instance is used should make it clear which. return.
     */
    public abstract double getMeanReturn();

    /**
     * The instrument's return variance. Subclasses must override either
     */
    public double getReturnVariance() {
        final double tmpVolatility = this.getVolatility();
        return tmpVolatility * tmpVolatility;
    }

    public final double getSharpeRatio() {
        return this.getSharpeRatio(null);
    }

    public final double getSharpeRatio(final Number riskFreeReturn) {
        if (riskFreeReturn != null) {
            return (this.getMeanReturn() - riskFreeReturn.doubleValue()) / this.getVolatility();
        }
        return this.getMeanReturn() / this.getVolatility();
    }

    /**
     * Value at Risk (VaR) is the maximum loss not exceeded with a given probability defined as the confidence
     * level, over a given period of time.
     */
    public final double getValueAtRisk(final Number confidenceLevel, final Number timePeriod) {

        final double aReturn = this.getMeanReturn();
        final double aStdDev = this.getVolatility();

        final double tmpConfidenceScale = SQRT_TWO * ErrorFunction.erfi(ONE - TWO * (ONE - confidenceLevel.doubleValue()));
        final double tmpTimePeriod = timePeriod.doubleValue();

        return PrimitiveMath.MAX.invoke(PrimitiveMath.SQRT.invoke(tmpTimePeriod) * aStdDev * tmpConfidenceScale - tmpTimePeriod * aReturn, ZERO);
    }

    public final double getValueAtRisk95() {
        return this.getValueAtRisk(0.95, ONE);
    }

    /**
     * Volatility refers to the standard deviation of the change in value of an asset with a specific time
     * horizon. It is often used to quantify the risk of the asset over that time period.
     */
    public double getVolatility() {
        return PrimitiveMath.SQRT.invoke(this.getReturnVariance());
    }

    /**
     * This method returns a list of the weights of the Portfolio's contained assets. An asset weight is NOT
     * restricted to being a share/percentage - it can be anything. Most subclasses do however assume that the
     * list of asset weights are shares/percentages that sum up to 100%. Calling normalise()
     * will transform any set of weights to that form.
     */
    public abstract List<BigDecimal> getWeights();

    /**
     * Normalised weights Portfolio
     */
    public final FinancePortfolio normalise() {
        return new NormalisedPortfolio(this, StandardType.PERCENT);
    }


    @Override
    public String toString() {
        return TypeUtils.format("{}: Return={}, Variance={}, Volatility={}, Weights={}", this.getClass().getSimpleName(), this.getMeanReturn(),
                this.getReturnVariance(), this.getVolatility(), this.getWeights());
    }

    protected abstract void reset();

}
