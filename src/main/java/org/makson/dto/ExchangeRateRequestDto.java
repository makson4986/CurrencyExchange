package org.makson.dto;

import org.makson.entities.CurrencyEntity;

import java.math.BigDecimal;

public record ExchangeRateRequestDto(String baseCurrencyCode, String  targetCurrencyCode,
                                     BigDecimal rate) {
}
