package com.cryptocurrency.service.finance.portfolio;

import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.type.context.NumberContext;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.ojalgo.function.constant.BigMath.ZERO;

/**
 * The Markowitz model, in this class, is defined as:
 * min (RAF/2) [w]<sup>T</sup>[C][w] - [w]<sup>T</sup>[r] <br>
 * subject to |[w]| = 1
 * RAF stands for Risk Aversion Factor. Instead of specifying a desired risk or return level you specify a
 * level of risk aversion that is used to balance the risk and return.
 * The expected returns for each of the assets must be excess returns. Otherwise this formulation is wrong.
 * The total weights of all assets will always be 100%, but shorting can be allowed or not according to your
 * preference. #setShortingAllowed(boolean) In addition you may set lower and upper limits on
 * any individual asset. #setLowerLimit(int, BigDecimal) and
 * Risk-free asset: That means there is no excess return and zero variance. Don't (try to) include a risk-free
 * asset here.
 * Do not worry about the minus sign in front of the return part of the objective function - it is
 * handled/negated for you. When you're asked to supply the expected excess returns you should supply
 * precisely that.
 * Basic usage instructions
 * After you've instantiated the MarkowitzModel you need to do one of three different things:
 * #setRiskAversion(Number) unless this was already set in the MarketEquilibrium or
 * FinancePortfolio.Context used to instantiate the MarkowitzModel
 * #setTargetReturn(BigDecimal)
 * #setTargetVariance(BigDecimal)
 * Optionally you may #setLowerLimit(int, BigDecimal),
 * #setUpperLimit(int, BigDecimal) or #setShortingAllowed(boolean).
 * To get the optimal asset weighs you simply call #getWeights() or #getAssetWeights().
 */
public final class MarkowitzModel extends OptimisedPortfolio {

    private static final double _0_0 = ZERO.doubleValue();
    private static final double INIT = PrimitiveMath.SQRT.invoke(PrimitiveMath.TEN);
    private static final double MAX = PrimitiveMath.HUNDRED * PrimitiveMath.HUNDRED;
    private static final double MIN = PrimitiveMath.HUNDREDTH;
    private static final NumberContext TARGET_CONTEXT = NumberContext.getGeneral(5, 4);

    private final HashMap<int[], LowerUpper> myConstraints = new HashMap<>();
    private transient ExpressionsBasedModel myOptimisationModel;
    private BigDecimal myTargetReturn;
    private BigDecimal myTargetVariance;

    public MarkowitzModel(final Primitive64Matrix covarianceMatrix, final Primitive64Matrix expectedExcessReturns) {
        super(covarianceMatrix, expectedExcessReturns);
    }

    public void setLowerLimit(final int assetIndex, final BigDecimal lowerLimit) {
        this.getVariable(assetIndex).lower(lowerLimit);
        this.reset();
    }

    public void setUpperLimit(final int assetIndex, final BigDecimal upperLimit) {
        this.getVariable(assetIndex).upper(upperLimit);
        this.reset();
    }

    @Override
    public String toString() {

        if (myOptimisationModel == null) {
            this.calculateAssetWeights();
        }

        return myOptimisationModel.toString();
    }

    private ExpressionsBasedModel generateOptimisationModel(final double riskAversion) {

        if (myOptimisationModel == null) {
            myOptimisationModel = this.makeModel(myConstraints);
        }

        myOptimisationModel.getExpression(VARIANCE).weight(riskAversion / 2.0);

        if (this.getOptimisationOptions().logger_appender != null) {
            BasicLogger.debug();
            BasicLogger.debug("@@@@@@@@@@@");
            BasicLogger.debug("Iteration RAF: {}", riskAversion);
            BasicLogger.debug("Iteration point: {}", myOptimisationModel.getVariableValues());
            BasicLogger.debug("@@@@@@@@@@@");
            BasicLogger.debug();
        }

        return myOptimisationModel;
    }

    /**
     * Constrained optimisation.
     */
    @Override
    protected Primitive64Matrix calculateAssetWeights() {

        if (this.getOptimisationOptions().logger_appender != null) {
            BasicLogger.debug();
            BasicLogger.debug("###################################################");
            BasicLogger.debug("BEGIN RAF: {} MarkowitzModel optimisation", this.getRiskAversion());
            BasicLogger.debug("###################################################");
            BasicLogger.debug();
        }

        Optimisation.Result tmpResult;

        if ((myTargetReturn != null) || (myTargetVariance != null)) {

            final double tmpTargetValue;
            if (myTargetVariance != null) {
                tmpTargetValue = myTargetVariance.doubleValue();
            } else if (myTargetReturn != null) {
                tmpTargetValue = myTargetReturn.doubleValue();
            } else {
                tmpTargetValue = _0_0;
            }

            tmpResult = this.generateOptimisationModel(_0_0).minimise();

            double tmpTargetNow = _0_0;
            double tmpTargetDiff = _0_0;
            double tmpTargetLast = _0_0;

            if (tmpResult.getState().isFeasible()) {

                double tmpCurrent;
                double tmpLow;
                double tmpHigh;
                if (this.isDefaultRiskAversion()) {
                    tmpCurrent = INIT;
                    tmpLow = MAX;
                    tmpHigh = MIN;
                } else {
                    tmpCurrent = this.getRiskAversion().doubleValue();
                    tmpLow = tmpCurrent * INIT;
                    tmpHigh = tmpCurrent / INIT;
                }

                do {

                    final ExpressionsBasedModel tmpModel = this.generateOptimisationModel(tmpCurrent);
                    tmpResult = tmpModel.minimise();

                    tmpTargetLast = tmpTargetNow;
                    if (myTargetVariance != null) {
                        tmpTargetNow = this.calculatePortfolioVariance(tmpResult).doubleValue();
                    } else if (myTargetReturn != null) {
                        tmpTargetNow = this.calculatePortfolioReturn(tmpResult, this.calculateAssetReturns()).doubleValue();
                    } else {
                        tmpTargetNow = tmpTargetValue;
                    }
                    tmpTargetDiff = tmpTargetNow - tmpTargetValue;

                    if (this.getOptimisationOptions().logger_appender != null) {
                        BasicLogger.debug();
                        BasicLogger.debug("RAF:   {}", tmpCurrent);
                        BasicLogger.debug("Last: {}", tmpTargetLast);
                        BasicLogger.debug("Now: {}", tmpTargetNow);
                        BasicLogger.debug("Target: {}", tmpTargetValue);
                        BasicLogger.debug("Diff:   {}", tmpTargetDiff);
                        BasicLogger.debug("Iteration:   {}", tmpResult);
                        BasicLogger.debug();
                    }

                    if (tmpTargetDiff < _0_0) {
                        tmpLow = tmpCurrent;
                    } else if (tmpTargetDiff > _0_0) {
                        tmpHigh = tmpCurrent;
                    }
                    tmpCurrent = PrimitiveMath.SQRT.invoke(tmpLow * tmpHigh);

                } while (!TARGET_CONTEXT.isSmall(tmpTargetValue, tmpTargetDiff) && TARGET_CONTEXT.isDifferent(tmpHigh, tmpLow));
            }

        } else
            tmpResult = this.generateOptimisationModel(this.getRiskAversion().doubleValue()).minimise();

        return this.handle(tmpResult);
    }

    @Override
    protected void reset() {
        super.reset();
        myOptimisationModel = null;
    }

    Scalar<?> calculatePortfolioReturn(final Access1D<?> weightsVctr, final Primitive64Matrix returnsVctr) {
        return super.calculatePortfolioReturn(MATRIX_FACTORY.columns(weightsVctr), returnsVctr);
    }

    Scalar<?> calculatePortfolioVariance(final Access1D<?> weightsVctr) {
        return super.calculatePortfolioVariance(MATRIX_FACTORY.columns(weightsVctr));
    }

}
