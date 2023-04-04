package com.cryptocurrency.service.finance.portfolio;

import org.ojalgo.function.constant.BigMath;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * SimpleAsset is used to describe 1 asset (portfolio member).
 */
public final class SimpleAsset extends FinancePortfolio {

    private final double myMeanReturn;
    private final double myVolatility;
    private final BigDecimal myWeight;

    public SimpleAsset(final Comparable<?> weight) {
        this(PrimitiveMath.ZERO, PrimitiveMath.ZERO, weight);
    }

    public SimpleAsset(final Comparable<?> meanReturn, final Comparable<?> volatility) {
        this(meanReturn, volatility, BigMath.ONE);
    }

    public SimpleAsset(final Comparable<?> meanReturn, final Comparable<?> volatility, final Comparable<?> weight) {

        super();

        myMeanReturn = meanReturn != null ? Scalar.doubleValue(meanReturn) : PrimitiveMath.ZERO;
        myVolatility = volatility != null ? Scalar.doubleValue(volatility) : PrimitiveMath.ZERO;
        myWeight = TypeUtils.toBigDecimal(weight);
    }

    @SuppressWarnings("unused")
    private SimpleAsset() {
        this(BigMath.ZERO, BigMath.ZERO, BigMath.ONE);
    }

    @Override
    public double getMeanReturn() {
        return myMeanReturn;
    }

    @Override
    public double getVolatility() {
        return myVolatility;
    }

    /**
     * Assuming there is precisely 1 weight - this class is used to describe 1 asset (portfolio member).
     */
    public BigDecimal getWeight() {
        return myWeight;
    }

    @Override
    public List<BigDecimal> getWeights() {
        return Collections.singletonList(myWeight);
    }

    @Override
    protected void reset() {

    }

}
