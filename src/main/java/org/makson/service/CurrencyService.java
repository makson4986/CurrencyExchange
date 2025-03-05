package org.makson.service;

import org.makson.CurrencyNotFoundException;
import org.makson.dao.CurrencyDao;
import org.makson.dto.CurrencyRequestDto;
import org.makson.dto.CurrencyResponseDto;
import org.makson.entity.CurrencyEntity;

import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final Long DEFAULT_CURRENCY_ID = 0L;

    private CurrencyService() {
    }

    public CurrencyResponseDto findByCode(String code) throws CurrencyNotFoundException {
        var optionalCurrency = currencyDao.findByCode(code);

        if (optionalCurrency.isPresent()) {
            CurrencyEntity currency = optionalCurrency.get();
            return new CurrencyResponseDto(
                    currency.getId(),
                    currency.getFullName(),
                    currency.getCode(),
                    currency.getSign()
            );
        } else {
            throw new CurrencyNotFoundException("Currency is not found!");
            //TODO exception
        }
    }

    public List<CurrencyResponseDto> findAll() {
        return currencyDao.findAll().stream()
                .map(currency -> new CurrencyResponseDto(
                        currency.getId(),
                        currency.getFullName(),
                        currency.getCode(),
                        currency.getSign()
                ))
                .toList();
    }

    public CurrencyResponseDto save(CurrencyRequestDto currencyDto) {
        CurrencyEntity newCurrency = currencyDao.save(new CurrencyEntity(
                DEFAULT_CURRENCY_ID,
                currencyDto.getCode(),
                currencyDto.getName(),
                currencyDto.getSign()));

        return new CurrencyResponseDto(
                newCurrency.getId(),
                newCurrency.getFullName(),
                newCurrency.getCode(),
                newCurrency.getSign());
    }


    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
