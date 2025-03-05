package org.makson.dao;

import org.makson.CurrencyNotFoundException;
import org.makson.entities.CurrencyEntity;
import org.makson.entities.ExchangeRateEntity;
import org.makson.utils.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements Dao<ExchangeRateEntity> {
    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final String FIND_ALL = """
            SELECT
                er.id,
                c1.code AS base_currency_code,
                c2.code AS target_currency_code,
                er.rate
            FROM
                exchange_rates er
            JOIN
                currencies c1 ON er.base_currency_id = c1.id
            JOIN
                currencies c2 ON er.target_currency_id = c2.id;
            """;

    private ExchangeRateDao() {

    }

    @Override
    public List<ExchangeRateEntity> findAll() {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = prepareStatement.executeQuery();
            List<ExchangeRateEntity> exchangeRates = new ArrayList<>();

            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }

            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExchangeRateEntity save(ExchangeRateEntity entity) {
        return null;
    }

    private ExchangeRateEntity buildExchangeRate(ResultSet resultSet) throws CurrencyNotFoundException {
        try {
            var baseCurrencyCode = currencyDao.findByCode(resultSet.getString("base_currency_code"));
            var targetCurrencyCode = currencyDao.findByCode(resultSet.getString("target_currency_code"));

            if (baseCurrencyCode.isPresent() && targetCurrencyCode.isPresent() ) {
                return new ExchangeRateEntity(
                        resultSet.getLong("id"),
                        baseCurrencyCode.get(),
                        targetCurrencyCode.get(),
                        resultSet.getBigDecimal("rate")
                );
            } else {
                throw new CurrencyNotFoundException("Currency is not found!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }
}
