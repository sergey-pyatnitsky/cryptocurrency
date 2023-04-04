package com.cryptocurrency.service.finance.portfolio;

import org.ojalgo.ProgrammingError;
import org.ojalgo.function.constant.BigMath;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.scalar.BigScalar;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class BlackLittermanModel extends EquilibriumModel {

    /**
     * View/Forecast/Opinion
     */
    private static final class View extends FinancePortfolio {

        private BigDecimal myMeanReturn = BigMath.ZERO;
        private final BlackLittermanModel myModel;
        private BigDecimal myReturnVariance = null;
        private BigDecimal myScale = null;
        private final List<BigDecimal> myWeights;

        public View(final BlackLittermanModel aModel, final List<BigDecimal> someWeights) {

            super();

            myModel = aModel;
            myWeights = someWeights;
        }

        @SuppressWarnings("unused")
        private View() {

            super();

            myModel = null;
            myWeights = null;

            ProgrammingError.throwForIllegalInvocation();
        }

        @Override
        public double getMeanReturn() {
            if (myMeanReturn != null) {
                return myMeanReturn.doubleValue();
            }
            return PrimitiveMath.ZERO;
        }

        @Override
        public double getReturnVariance() {

            if (myReturnVariance != null) {

                return myReturnVariance.doubleValue();

            }
            final Primitive64Matrix tmpWeights = MATRIX_FACTORY.columns(myWeights);

            BigDecimal retVal = myModel.calculateVariance(tmpWeights);

            if (myScale != null) {

                retVal = retVal.multiply(myScale);

            } else {

                retVal = retVal.multiply(myModel.getConfidence().toBigDecimal());
            }

            return retVal.doubleValue();
        }

        @Override
        public List<BigDecimal> getWeights() {
            return myWeights;
        }

        @Override
        protected void reset() {
        }

        protected void setMeanReturn(final BigDecimal aMeanReturn) {
            myMeanReturn = aMeanReturn;
        }

        protected void setReturnVariance(final BigDecimal aReturnVariance) {
            myReturnVariance = aReturnVariance;
        }

        protected void setScale(final BigDecimal aScale) {
            myScale = aScale;
        }

    }

    private BigDecimal myConfidence = BigMath.ONE;
    private final Primitive64Matrix myOriginalWeights;
    private final List<FinancePortfolio> myViews;

    public BlackLittermanModel(final Context context, final FinancePortfolio originalWeights) {

        super(context);

        myOriginalWeights = MATRIX_FACTORY.columns(originalWeights.getWeights());
        myViews = new ArrayList<>();
    }

    /**
     * @param marketEquilibrium The covariance matrix, and market risk aversion
     * @param originalWeights The market portfolio
     */
    public BlackLittermanModel(final MarketEquilibrium marketEquilibrium, final Primitive64Matrix originalWeights) {

        super(marketEquilibrium);

        myOriginalWeights = originalWeights;
        myViews = new ArrayList<>();
    }

    private BlackLittermanModel(final MarketEquilibrium aMarketEquilibrium) {

        super(aMarketEquilibrium);

        myOriginalWeights = null;
        myViews = null;

        ProgrammingError.throwForIllegalInvocation();
    }

    public void addViewWithStandardDeviation(final List<BigDecimal> someWeights, final BigDecimal aReturn, final BigDecimal aStdDev) {

        final View tmpView = new View(this, someWeights);

        tmpView.setMeanReturn(aReturn);
        tmpView.setReturnVariance(aStdDev.multiply(aStdDev));
        tmpView.setScale(null);

        myViews.add(tmpView);
    }

    /**
     * "weight on views" or "tau" A parameter that describes the general confidence in the views. Typically
     * set to sometghing between 0.0 and 1.0. 0.0 = "No confidence!" Why bother... 1.0 = As confident as the
     * market. This is highly unlikely.
     */
    public Scalar<?> getConfidence() {
        return BigScalar.of(myConfidence);
    }

    @Override
    protected Primitive64Matrix calculateAssetReturns() {
        return this.calculateAssetReturns(this.calculateAssetWeights());
    }

    @Override
    protected Primitive64Matrix calculateAssetWeights() {

        final Primitive64Matrix tmpViewPortfolios = this.getViewPortfolios();
        final Primitive64Matrix tmpViewReturns = this.getViewReturns();
        final Primitive64Matrix tmpViewVariances = this.getViewVariances();

        final Primitive64Matrix tmpCovariances = this.getCovariances();

        final Primitive64Matrix tmpRightParenthesis = tmpViewReturns.subtract(tmpViewPortfolios.multiply(tmpCovariances).multiply(myOriginalWeights));

        final Primitive64Matrix tmpViewsTransposed = tmpViewPortfolios.transpose();

        final Primitive64Matrix tmpLeftParenthesis = tmpViewVariances.add(tmpViewPortfolios.multiply(tmpCovariances).multiply(tmpViewsTransposed));

        return myOriginalWeights.add(tmpViewsTransposed.multiply(tmpLeftParenthesis.solve(tmpRightParenthesis)));
    }

    protected Primitive64Matrix getViewPortfolios() {

        final int tmpRowDim = myViews.size();
        final int tmpColDim = (int) myOriginalWeights.count();

        final Primitive64Matrix.DenseReceiver retVal = MATRIX_FACTORY.makeDense(tmpRowDim, tmpColDim);

        FinancePortfolio tmpView;
        List<BigDecimal> tmpWeights;

        for (int i = 0; i < tmpRowDim; i++) {

            tmpView = myViews.get(i);
            tmpWeights = tmpView.getWeights();

            for (int j = 0; j < tmpColDim; j++) {
                retVal.set(i, j, tmpWeights.get(j));
            }
        }

        return retVal.build();
    }

    /**
     * Scaled by risk aversion factor.
     */
    protected Primitive64Matrix getViewReturns() {

        final int tmpRowDim = myViews.size();
        final int tmpColDim = 1;

        final Primitive64Matrix.DenseReceiver retVal = MATRIX_FACTORY.makeDense(tmpRowDim, tmpColDim);

        double tmpRet;
        final double tmpRAF = this.getRiskAversion().doubleValue();

        for (int i = 0; i < tmpRowDim; i++) {

            tmpRet = myViews.get(i).getMeanReturn();

            retVal.set(i, 0, PrimitiveMath.DIVIDE.invoke(tmpRet, tmpRAF));
        }

        return retVal.build();
    }

    /**
     * Scaled by tau / weight on views
     */
    protected Primitive64Matrix getViewVariances() {

        final int tmpDim = myViews.size();

        final Primitive64Matrix.DenseReceiver retVal = MATRIX_FACTORY.makeDense(tmpDim, tmpDim);

        if (myConfidence.compareTo(BigMath.ONE) == 0) {

            for (int ij = 0; ij < tmpDim; ij++) {
                retVal.set(ij, ij, myViews.get(ij).getReturnVariance());
            }

        } else {

            final double tmpScale = myConfidence.doubleValue();

            double tmpVar;
            for (int ij = 0; ij < tmpDim; ij++) {

                tmpVar = myViews.get(ij).getReturnVariance();

                retVal.set(ij, ij, PrimitiveMath.DIVIDE.invoke(tmpVar, tmpScale));
            }
        }

        return retVal.build();
    }

    BigDecimal calculateVariance(final Primitive64Matrix weights) {

        Primitive64Matrix tmpVal = this.getCovariances();

        tmpVal = tmpVal.multiply(weights);

        return TypeUtils.toBigDecimal(weights.transpose().multiply(tmpVal).get(0, 0));
    }
}
