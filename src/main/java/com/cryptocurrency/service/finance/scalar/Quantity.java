package com.cryptocurrency.service.finance.scalar;

import org.ojalgo.scalar.ExactDecimal;
import org.ojalgo.scalar.Scalar;

final class Quantity extends ExactDecimal<Quantity> {

    public static final Descriptor DESCRIPTOR = new Descriptor(6);

    public static final Scalar.Factory<Quantity> FACTORY = new ExactDecimal.Factory<Quantity>() {

        public Quantity cast(final double value) {
            return Quantity.valueOf(value);
        }

        public Quantity cast(final Comparable<?> number) {
            return Quantity.valueOf(number);
        }

        public Quantity convert(final double value) {
            return Quantity.valueOf(value);
        }

        public Quantity convert(final Comparable<?> number) {
            return Quantity.valueOf(number);
        }

        public Descriptor descriptor() {
            return DESCRIPTOR;
        }

        public Quantity one() {
            return ONE;
        }

        public Quantity zero() {
            return ZERO;
        }

    };

    private static final double DOUBLE_DENOMINATOR = 1_000_000D;
    private static final long LONG_DENOMINATOR = 1_000_000L;

    public static final Quantity NEG = new Quantity(-LONG_DENOMINATOR);
    public static final Quantity ONE = new Quantity(LONG_DENOMINATOR);
    public static final Quantity TWO = new Quantity(LONG_DENOMINATOR + LONG_DENOMINATOR);
    public static final Quantity ZERO = new Quantity();

    public static Quantity valueOf(final double value) {
        return new Quantity(Math.round(value * DOUBLE_DENOMINATOR));
    }

    public static Quantity valueOf(final Comparable<?> number) {

        if (number == null) {
            return ZERO;
        }

        if (number instanceof Quantity) {
            return (Quantity) number;
        }

        return Quantity.valueOf(Scalar.doubleValue(number));
    }

    public Quantity() {
        super(0L);
    }

    Quantity(final long numerator) {
        super(numerator);
    }

    public Amount multiply(final Price price) {
        return new Amount(Amount.DESCRIPTOR.multiply(this, price));
    }

    @Override
    protected Descriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    protected Quantity wrap(final long numerator) {
        return new Quantity(numerator);
    }

}
