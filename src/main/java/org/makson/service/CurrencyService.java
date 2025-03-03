package org.makson.service;

import org.makson.dao.CurrencyDao;
import org.makson.dto.CurrencyDto;
import org.makson.entity.CurrencyEntity;

import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final Long DEFAULT_CURRENCY_ID = 0L;

    private CurrencyService() {
    }

    public List<CurrencyEntity> findAll() {
        return currencyDao.findAll().stream()
                .map(currency -> new CurrencyEntity(
                        currency.getId(),
                        currency.getFullName(),
                        currency.getCode(),
                        currency.getSign()
                ))
                .toList();
    }

    public CurrencyDto save(CurrencyDto currencyDto) {
        CurrencyEntity newCurrency = currencyDao.save(new CurrencyEntity(
                DEFAULT_CURRENCY_ID,
                currencyDto.getCode(),
                currencyDto.getName(),
                currencyDto.getSign()));

        return new CurrencyDto(
                newCurrency.getFullName(),
                newCurrency.getCode(),
                newCurrency.getSign());
    }


    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
