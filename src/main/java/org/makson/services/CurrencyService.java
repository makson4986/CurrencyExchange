package org.makson.services;

import org.makson.exception.CurrencyAlreadyExistsException;
import org.makson.exception.CurrencyNotFoundException;
import org.makson.dao.CurrencyDao;
import org.makson.dto.CurrencyRequestDto;
import org.makson.dto.CurrencyResponseDto;
import org.makson.entities.CurrencyEntity;

import java.util.List;
import java.util.Optional;

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
            throw new CurrencyNotFoundException();
            //TODO exception надо свое или нет
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

    public CurrencyResponseDto save(CurrencyRequestDto currencyDto) throws CurrencyAlreadyExistsException {
        var newCurrency = currencyDao.save(new CurrencyEntity(
                DEFAULT_CURRENCY_ID,
                currencyDto.code(),
                currencyDto.name(),
                currencyDto.sign()));

        if (newCurrency.isPresent()) {
            return new CurrencyResponseDto(
                    newCurrency.get().getId(),
                    newCurrency.get().getFullName(),
                    newCurrency.get().getCode(),
                    newCurrency.get().getSign());
        } else {
            throw new CurrencyAlreadyExistsException();
        }
    }


    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
