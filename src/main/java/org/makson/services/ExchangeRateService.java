package org.makson.services;

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

    public ExchangeRateResponseDto findByExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws CurrencyNotFoundException, ExchangeRateNotFoundException {
        var optionalExchangeRate = exchangeRateDao.findByExchangeRate(baseCurrencyCode, targetCurrencyCode);

        if (optionalExchangeRate.isPresent()) {
            ExchangeRateEntity exchangeRate = optionalExchangeRate.get();
            return new ExchangeRateResponseDto(
                    exchangeRate.getId(),
                    exchangeRate.getBaseCurrency(),
                    exchangeRate.getTargetCurrency(),
                    exchangeRate.getRate()
            );
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
            var newExchangeRate = exchangeRateDao.save(new ExchangeRateEntity(
                    DEFAULT_EXCHANGE_RATE_ID,
                    baseCurrency.get(),
                    targetCurrency.get(),
                    exchangeRateRequestDto.rate()
            ));

            if (newExchangeRate.isPresent()) {
                return new ExchangeRateResponseDto(
                        newExchangeRate.get().getId(),
                        newExchangeRate.get().getBaseCurrency(),
                        newExchangeRate.get().getTargetCurrency(),
                        newExchangeRate.get().getRate()
                );
            } else {
                throw new ExchangeRateAlreadyExistsException();
            }
        } else {
            throw new CurrencyNotFoundException();
        }
    }

    public ExchangeRateResponseDto update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws ExchangeRateNotFoundException {
        var updatedExchangeRate = exchangeRateDao.update(baseCurrencyCode, targetCurrencyCode, rate);

        if (updatedExchangeRate.isPresent()) {
            return new ExchangeRateResponseDto(
                    updatedExchangeRate.get().getId(),
                    updatedExchangeRate.get().getBaseCurrency(),
                    updatedExchangeRate.get().getTargetCurrency(),
                    updatedExchangeRate.get().getRate()
            );
        } else {
            throw new ExchangeRateNotFoundException();
        }
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
