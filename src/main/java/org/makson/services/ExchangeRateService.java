package org.makson.services;

import org.makson.dao.ExchangeRateDao;
import org.makson.dto.ExchangeRateResponseDto;
import org.makson.entities.ExchangeRateEntity;

import java.util.List;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    private ExchangeRateService() {
    }

    public List<ExchangeRateResponseDto> findAll() {
        return exchangeRateDao.findAll().stream()
                .map(exchangeRate -> new ExchangeRateResponseDto(
                        exchangeRate.getId(),
                        exchangeRate.getBaseCurrencyId(),
                        exchangeRate.getTargetCurrencyId(),
                        exchangeRate.getRate()
                ))
                .toList();


    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
