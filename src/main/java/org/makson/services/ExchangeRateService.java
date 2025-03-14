package org.makson.services;

import org.makson.dao.CurrencyDao;
import org.makson.dao.ExchangeRateDao;
import org.makson.dto.ConvertCurrencyRequestDto;
import org.makson.dto.ConvertCurrencyResponseDto;
import org.makson.dto.ExchangeRateRequestDto;
import org.makson.dto.ExchangeRateResponseDto;
import org.makson.entities.CurrencyEntity;
import org.makson.entities.ExchangeRateEntity;
import org.makson.exception.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeRateService() {
    }

    public ExchangeRateResponseDto findByExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws DataNotFoundException, SQLException {
        ExchangeRateEntity exchangeRate = exchangeRateDao.findByExchangeRate(baseCurrencyCode, targetCurrencyCode);
        return new ExchangeRateResponseDto(
                exchangeRate.getId(),
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate()
        );
    }

    public List<ExchangeRateResponseDto> findAll() throws SQLException {
        return exchangeRateDao.findAll().stream()
                .map(exchangeRate -> new ExchangeRateResponseDto(
                        exchangeRate.getId(),
                        exchangeRate.getBaseCurrency(),
                        exchangeRate.getTargetCurrency(),
                        exchangeRate.getRate()
                ))
                .toList();
    }

    public ExchangeRateResponseDto save(ExchangeRateRequestDto exchangeRateRequestDto) throws DataNotFoundException, DataAlreadyExistException, SQLException {
        ExchangeRateEntity newExchangeRate = exchangeRateDao.save(new ExchangeRateEntity(
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

    public ExchangeRateResponseDto update(ExchangeRateRequestDto exchangeRateRequestDto) throws DataNotFoundException, SQLException {
        ExchangeRateEntity updatedExchangeRate;
        updatedExchangeRate = exchangeRateDao.update(new ExchangeRateEntity(
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

    public ConvertCurrencyResponseDto convertCurrency(ConvertCurrencyRequestDto convertCurrencyRequestDto) throws DataNotFoundException, SQLException {
        CurrencyEntity baseCurrency;
        CurrencyEntity targetCurrency;
        BigDecimal rate;

        try {
            var exchangeRate = findByExchangeRate(convertCurrencyRequestDto.baseCurrencyCode(), convertCurrencyRequestDto.targetCurrencyCode());
            baseCurrency = exchangeRate.baseCurrency();
            targetCurrency = exchangeRate.targetCurrency();
            rate = exchangeRate.rate();
        } catch (DataNotFoundException _) {
            try {
                var exchangeRate = findByExchangeRate(convertCurrencyRequestDto.targetCurrencyCode(), convertCurrencyRequestDto.baseCurrencyCode());

                baseCurrency = exchangeRate.baseCurrency();
                targetCurrency = exchangeRate.targetCurrency();
                rate = new BigDecimal(1).divide(exchangeRate.rate(), 6, RoundingMode.HALF_UP);
            } catch (DataNotFoundException _) {
                var exchangeRate1 = findByExchangeRate("USD", convertCurrencyRequestDto.baseCurrencyCode());
                var exchangeRate2 = findByExchangeRate("USD", convertCurrencyRequestDto.targetCurrencyCode());

                baseCurrency = exchangeRate1.targetCurrency();
                targetCurrency = exchangeRate2.targetCurrency();
                rate = exchangeRate2.rate().divide(exchangeRate1.rate(), 6, RoundingMode.HALF_UP);
            }
        }

        BigDecimal amount = convertCurrencyRequestDto.amount();
        return new ConvertCurrencyResponseDto(
                baseCurrency,
                targetCurrency,
                rate,
                amount,
                rate.multiply(amount)
        );

    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
