package org.makson.dto;

import org.makson.entities.CurrencyEntity;

import java.math.BigDecimal;

public record ConvertCurrencyResponseDto(CurrencyEntity baseCurrency, CurrencyEntity targetCurrency, BigDecimal rate,
                                         BigDecimal amount, BigDecimal convertedAmount) {
}
