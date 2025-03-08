package org.makson.services;

import org.makson.exception.CurrencyNotFoundException;
import org.makson.dao.CurrencyDao;
import org.makson.dao.ExchangeRateDao;
import org.makson.dto.ExchangeRateRequestDto;
import org.makson.dto.ExchangeRateResponseDto;
import org.makson.entities.ExchangeRateEntity;
import org.makson.exception.ExchangeRateNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final Long DEFAULT_EXCHANGE_RATE_ID = 0L;

    private ExchangeRateService() {
    }

    public ExchangeRateResponseDto findByExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws CurrencyNotFoundException, ExchangeRateNotFoundException {
        var optionalExchangeRate = exchangeRateDao.findByExchangeRate(baseCurrencyCode, targetCurrencyCode);

        if (optionalExchangeRate.isPresent()) {
            ExchangeRateEntity exchangeRate = optionalExchangeRate.get();
            try {
                return new ExchangeRateResponseDto(
                        exchangeRate.getId(),
                        exchangeRate.getBaseCurrency(),
                        exchangeRate.getTargetCurrency(),
                        exchangeRate.getRate()
                );
            } catch (NullPointerException e) {
                throw new CurrencyNotFoundException(e);
            }
        } else {
            throw new ExchangeRateNotFoundException();
        }
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

    public ExchangeRateResponseDto save(ExchangeRateRequestDto exchangeRateRequestDto) throws CurrencyNotFoundException {
        var baseCurrency = currencyDao.findByCode(exchangeRateRequestDto.baseCurrencyCode());
        var targetCurrency = currencyDao.findByCode(exchangeRateRequestDto.targetCurrencyCode());

        if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
            ExchangeRateEntity exchangeRate = exchangeRateDao.save(new ExchangeRateEntity(
                    DEFAULT_EXCHANGE_RATE_ID,
                    baseCurrency.get(),
                    targetCurrency.get(),
                    exchangeRateRequestDto.rate()
            ));

            return new ExchangeRateResponseDto(
                    exchangeRate.getId(),
                    exchangeRate.getBaseCurrency(),
                    exchangeRate.getTargetCurrency(),
                    exchangeRate.getRate()
            );
        } else {
            throw new CurrencyNotFoundException();
        }
    }

    public ExchangeRateResponseDto update(BigDecimal rate) {
        ExchangeRateEntity updatedExchangeRate = exchangeRateDao.update(rate);

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
