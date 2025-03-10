package org.makson.utils;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public final class CurrencyValidator {
    private static final Set<String> currencyCodes;

    static {
        currencyCodes = Currency.getAvailableCurrencies().stream()
                .map(Currency::getCurrencyCode)
                .collect(Collectors.toSet());
    }

    private CurrencyValidator() {
    }

    public static boolean isValidCurrencyCode(String code) {
        return currencyCodes.contains(code);
    }
}
