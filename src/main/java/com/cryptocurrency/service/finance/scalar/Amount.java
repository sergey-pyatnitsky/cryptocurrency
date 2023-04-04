package com.cryptocurrency.service.finance.scalar;

import org.ojalgo.scalar.ExactDecimal;
import org.ojalgo.scalar.Scalar;

final class Amount extends ExactDecimal<Amount> {

    public static final Descriptor DESCRIPTOR = new Descriptor(2);

    public static final Scalar.Factory<Amount> FACTORY = new ExactDecimal.Factory<Amount>() {

        public Amount cast(final double value) {
            return Amount.valueOf(value);
        }

        public Amount cast(final Comparable<?> number) {
            return Amount.valueOf(number);
        }

        public Amount convert(final double value) {
            return Amount.valueOf(value);
        }

        public Amount convert(final Comparable<?> number) {
            return Amount.valueOf(number);
        }

        public Descriptor descriptor() {
            return DESCRIPTOR;
        }

        public Amount one() {
            return ONE;
        }

        public Amount zero() {
            return ZERO;
        }

    };

    private static final double DOUBLE_DENOMINATOR = 100D;
    private static final long LONG_DENOMINATOR = 100L;

    public static final Amount NEG = new Amount(-LONG_DENOMINATOR);
    public static final Amount ONE = new Amount(LONG_DENOMINATOR);
    public static final Amount TWO = new Amount(LONG_DENOMINATOR + LONG_DENOMINATOR);
    public static final Amount ZERO = new Amount();

    public static Amount valueOf(final double value) {
        return new Amount(Math.round(value * DOUBLE_DENOMINATOR));
    }

    public static Amount valueOf(final Comparable<?> number) {

        if (number == null) {
            return ZERO;
        }

        if (number instanceof Amount) {
            return (Amount) number;
        }

        return Amount.valueOf(Scalar.doubleValue(number));
    }

    public Amount() {
        super(0L);
    }

    Amount(final long numerator) {
        super(numerator);
    }

    public Quantity divide(final Price price) {
        return new Quantity(Quantity.DESCRIPTOR.multiply(this, price));
    }

    public Price divide(final Quantity quanntity) {
        return new Price(Price.DESCRIPTOR.multiply(this, quanntity));
    }

    public Amount multiply(final Price rate) {
        return new Amount(Amount.DESCRIPTOR.multiply(this, rate));
    }

    @Override
    protected Descriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    protected Amount wrap(final long numerator) {
        return new Amount(numerator);
    }

}
