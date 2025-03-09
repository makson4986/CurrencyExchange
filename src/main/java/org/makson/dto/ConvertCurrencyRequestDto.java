package org.makson.dto;

import java.math.BigDecimal;

public record ConvertCurrencyRequestDto(String baseCurrencyCode, String targetCurrencyCode,
                                        BigDecimal amount) {
}
