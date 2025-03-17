package org.makson.utils;

import java.math.BigDecimal;

public final class DataValidator {
    private static final Integer MAX_LENGTH_DATA = 64;
    private static final BigDecimal MAX_VALUE_DATA = new BigDecimal(10000);
    private static final BigDecimal MIN_VALUE_DATA = new BigDecimal(0);

    private DataValidator() {
    }

    public static boolean isValidParameter(String parameter) {
        return !(parameter == null || parameter.isBlank()) && parameter.length() <= MAX_LENGTH_DATA;
    }

    public static void isValidNumericalValueParameter(String parameter) throws NumberFormatException {
        BigDecimal value = new BigDecimal(parameter);

        if ((value.compareTo(MAX_VALUE_DATA) > 0) || (value.compareTo(MIN_VALUE_DATA) < 0)) {
            throw new NumberFormatException();
        }
    }
}
