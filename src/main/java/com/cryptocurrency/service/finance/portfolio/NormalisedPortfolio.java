package com.cryptocurrency.service.finance.portfolio;

import org.ojalgo.ProgrammingError;
import org.ojalgo.function.constant.BigMath;
import org.ojalgo.type.context.NumberContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Normalised weights Portfolio
 */
final class NormalisedPortfolio extends FinancePortfolio {

    private final FinancePortfolio myBasePortfolio;
    private transient BigDecimal myTotalWeight;
    private final NumberContext myWeightsContext;

    @SuppressWarnings("unused")
    private NormalisedPortfolio() {

        this(null, null);

        ProgrammingError.throwForIllegalInvocation();
    }

    NormalisedPortfolio(final FinancePortfolio basePortfolio, final NumberContext weightsContext) {

        super();

        myBasePortfolio = basePortfolio;
        myWeightsContext = weightsContext;
    }

    @Override
    public double getMeanReturn() {
        return myBasePortfolio.getMeanReturn() / this.getTotalWeight().doubleValue();
    }

    @Override
    public double getVolatility() {
        return myBasePortfolio.getVolatility() / this.getTotalWeight().doubleValue();
    }

    @Override
    public List<BigDecimal> getWeights() {

        final List<BigDecimal> retVal = new ArrayList<>();

        final BigDecimal tmpTotalWeight = this.getTotalWeight();

        BigDecimal tmpSum = BigMath.ZERO;
        BigDecimal tmpLargest = BigMath.ZERO;
        int tmpIndexOfLargest = -1;

        final List<BigDecimal> tmpWeights = myBasePortfolio.getWeights();
        BigDecimal tmpWeight;
        for (int i = 0; i < tmpWeights.size(); i++) {

            tmpWeight = tmpWeights.get(i);
            tmpWeight = BigMath.DIVIDE.invoke(tmpWeight, tmpTotalWeight);
            tmpWeight = myWeightsContext.enforce(tmpWeight);

            retVal.add(tmpWeight);

            tmpSum = tmpSum.add(tmpWeight);

            if (tmpWeight.abs().compareTo(tmpLargest) == 1) {
                tmpLargest = tmpWeight.abs();
                tmpIndexOfLargest = i;
            }
        }

        if ((tmpSum.compareTo(BigMath.ONE) != 0) && (tmpIndexOfLargest != -1)) {
            retVal.set(tmpIndexOfLargest, retVal.get(tmpIndexOfLargest).subtract(tmpSum.subtract(BigMath.ONE)));
        }

        return retVal;
    }

    private BigDecimal getTotalWeight() {

        if (myTotalWeight == null) {
            myTotalWeight = BigMath.ZERO;
            for (final BigDecimal tmpWeight : myBasePortfolio.getWeights()) {
                myTotalWeight = myTotalWeight.add(tmpWeight);
            }
            if (myTotalWeight.signum() == 0) {
                myTotalWeight = BigMath.ONE;
            }
        }

        return myTotalWeight;
    }

    @Override
    protected void reset() {

        myBasePortfolio.reset();

        myTotalWeight = null;
    }

}
