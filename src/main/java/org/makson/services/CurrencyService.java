package org.makson.services;

import org.makson.dao.CurrencyDao;
import org.makson.dto.CurrencyRequestDto;
import org.makson.dto.CurrencyResponseDto;
import org.makson.entities.CurrencyEntity;
import org.makson.exception.DataAlreadyExistException;
import org.makson.exception.DataNotFoundException;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public CurrencyResponseDto findByCode(String code) throws DataNotFoundException, SQLException {
        CurrencyEntity currency = currencyDao.findByCode(code);
        return new CurrencyResponseDto(
                currency.getId(),
                currency.getFullName(),
                currency.getCode(),
                currency.getSign()
        );
    }

    public List<CurrencyResponseDto> findAll() throws SQLException {
        return currencyDao.findAll().stream()
                .map(currency -> new CurrencyResponseDto(
                        currency.getId(),
                        currency.getFullName(),
                        currency.getCode(),
                        currency.getSign()
                ))
                .toList();
    }

    public CurrencyResponseDto save(CurrencyRequestDto currencyDto) throws DataAlreadyExistException, SQLException {
        CurrencyEntity newCurrency = currencyDao.save(new CurrencyEntity(
                currencyDto.code(),
                currencyDto.name(),
                currencyDto.sign()));

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
