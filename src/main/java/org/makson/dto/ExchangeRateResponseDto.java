package org.makson.dto;

import org.makson.entities.CurrencyEntity;

import java.math.BigDecimal;

public record ExchangeRateResponseDto(Long id, CurrencyEntity baseCurrency, CurrencyEntity targetCurrency,
                                      BigDecimal rate) {
}
