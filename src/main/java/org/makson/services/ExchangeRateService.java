package org.makson.services;

import org.makson.entities.CurrencyEntity;
import org.makson.exception.CurrencyNotFoundException;
import org.makson.dao.CurrencyDao;
import org.makson.dao.ExchangeRateDao;
import org.makson.dto.ExchangeRateRequestDto;
import org.makson.dto.ExchangeRateResponseDto;
import org.makson.entities.ExchangeRateEntity;
import org.makson.exception.ExchangeRateAlreadyExistsException;
import org.makson.exception.ExchangeRateNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final Long DEFAULT_EXCHANGE_RATE_ID = 0L;

    private ExchangeRateService() {
    }

    public ExchangeRateResponseDto findByExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws ExchangeRateNotFoundException {
        ExchangeRateEntity exchangeRate = exchangeRateDao.findByExchangeRate(baseCurrencyCode, targetCurrencyCode);
        return new ExchangeRateResponseDto(
                exchangeRate.getId(),
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate()
        );
    }

    public List<ExchangeRateResponseDto> findAll() {
        return exchangeRateDao.findAll().stream()
                .map(exchangeRate -> new ExchangeRateResponseDto(
                        exchangeRate.getId(),
                        exchangeRate.getBaseCurrency(),
                        exchangeRate.getTargetCurrency(),
                        exchangeRate.getRate()
                ))
                .toList();
    }

    public ExchangeRateResponseDto save(ExchangeRateRequestDto exchangeRateRequestDto) throws CurrencyNotFoundException, ExchangeRateAlreadyExistsException {
        ExchangeRateEntity newExchangeRate = exchangeRateDao.save(new ExchangeRateEntity(
                DEFAULT_EXCHANGE_RATE_ID,
                currencyDao.findByCode(exchangeRateRequestDto.baseCurrencyCode()),
                currencyDao.findByCode(exchangeRateRequestDto.targetCurrencyCode()),
                exchangeRateRequestDto.rate()
        ));
        return new ExchangeRateResponseDto(
                newExchangeRate.getId(),
                newExchangeRate.getBaseCurrency(),
                newExchangeRate.getTargetCurrency(),
                newExchangeRate.getRate()
        );
    }

    public ExchangeRateResponseDto update(ExchangeRateRequestDto exchangeRateRequestDto) throws ExchangeRateNotFoundException {
        ExchangeRateEntity updatedExchangeRate = exchangeRateDao.update(new ExchangeRateEntity(
                DEFAULT_EXCHANGE_RATE_ID,
                currencyDao.findByCode(exchangeRateRequestDto.baseCurrencyCode()),
                currencyDao.findByCode(exchangeRateRequestDto.targetCurrencyCode()),
                exchangeRateRequestDto.rate()
        ));

        return new ExchangeRateResponseDto(
                updatedExchangeRate.getId(),
                updatedExchangeRate.getBaseCurrency(),
                updatedExchangeRate.getTargetCurrency(),
                updatedExchangeRate.getRate()
        );
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
