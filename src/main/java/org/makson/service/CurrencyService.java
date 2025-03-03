package org.makson.service;

import org.makson.dao.CurrencyDao;
import org.makson.dto.CurrencyDto;
import org.makson.entity.CurrencyEntity;

import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public List<CurrencyDto> findAll() {
        return currencyDao.findAll().stream()
                .map(currency -> new CurrencyDto(
                        currency.getId(),
                        currency.getFullName(),
                        currency.getCode(),
                        currency.getSign()
                ))
                .toList();
    }

    public CurrencyDto save(CurrencyDto currencyDto) {
        CurrencyEntity newCurrency = currencyDao.save(new CurrencyEntity(
                currencyDto.getId(),
                currencyDto.getCode(),
                currencyDto.getName(),
                currencyDto.getSign()));

        return new CurrencyDto(newCurrency.getId(),
                newCurrency.getFullName(),
                newCurrency.getCode(),
                newCurrency.getSign());
    }


    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
