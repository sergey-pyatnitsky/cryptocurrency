package com.cryptocurrency.service.finance.scalar;

import org.ojalgo.scalar.ExactDecimal;
import org.ojalgo.scalar.Scalar;

final class Price extends ExactDecimal<Price> {

    public static final Descriptor DESCRIPTOR = new Descriptor(8);

    public static final Scalar.Factory<Price> FACTORY = new ExactDecimal.Factory<Price>() {

        public Price cast(final double value) {
            return Price.valueOf(value);
        }

        public Price cast(final Comparable<?> number) {
            return Price.valueOf(number);
        }

        public Price convert(final double value) {
            return Price.valueOf(value);
        }

        public Price convert(final Comparable<?> number) {
            return Price.valueOf(number);
        }

        public Descriptor descriptor() {
            return DESCRIPTOR;
        }

        public Price one() {
            return ONE;
        }

        public Price zero() {
            return ZERO;
        }

    };

    private static final double DOUBLE_DENOMINATOR = 100_000_000D;
    private static final long LONG_DENOMINATOR = 100_000_000L;

    public static final Price NEG = new Price(-LONG_DENOMINATOR);
    public static final Price ONE = new Price(LONG_DENOMINATOR);
    public static final Price TWO = new Price(LONG_DENOMINATOR + LONG_DENOMINATOR);
    public static final Price ZERO = new Price();

    public static Price valueOf(final double value) {
        return new Price(Math.round(value * DOUBLE_DENOMINATOR));
    }

    public static Price valueOf(final Comparable<?> number) {

        if (number == null) {
            return ZERO;
        }

        if (number instanceof Price) {
            return (Price) number;
        }

        return Price.valueOf(Scalar.doubleValue(number));
    }

    public Price() {
        super(0L);
    }

    Price(final long numerator) {
        super(numerator);
    }

    public Amount multiply(final Quantity quanntity) {
        return new Amount(Amount.DESCRIPTOR.multiply(this, quanntity));
    }

    @Override
    protected Descriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    protected Price wrap(final long numerator) {
        return new Price(numerator);
    }

}
