package org.makson.dto;

import org.makson.entities.CurrencyEntity;

import java.math.BigDecimal;

public record ExchangeRateRequestDto(CurrencyEntity baseCurrency, CurrencyEntity targetCurrency,
                                     BigDecimal rate) {
}
