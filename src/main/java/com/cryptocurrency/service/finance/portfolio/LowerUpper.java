
package com.cryptocurrency.service.finance.portfolio;

import org.ojalgo.type.TypeUtils;

import java.math.BigDecimal;

final class LowerUpper {
    final BigDecimal lower;
    final BigDecimal upper;

    LowerUpper(final Comparable<?> someLower, final Comparable<?> someUpper) {
        super();
        lower = someLower != null ? TypeUtils.toBigDecimal(someLower) : null;
        upper = someUpper != null ? TypeUtils.toBigDecimal(someUpper) : null;
    }
}
