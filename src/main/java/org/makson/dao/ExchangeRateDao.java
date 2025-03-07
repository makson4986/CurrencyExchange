package org.makson.dao;

import org.makson.CurrencyNotFoundException;
import org.makson.entities.ExchangeRateEntity;
import org.makson.utils.ConnectionManager;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao implements Dao<ExchangeRateEntity> {
    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
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

    private final String INSERT = """
            INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?)
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
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setLong(1, entity.getBaseCurrency().getId());
            prepareStatement.setLong(2, entity.getTargetCurrency().getId());
            prepareStatement.setBigDecimal(3, entity.getRate());

            prepareStatement.executeUpdate();

            try (var generatedKeys = prepareStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }

            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ExchangeRateEntity buildExchangeRate(ResultSet resultSet) throws SQLException {
        //Результат CurrencyDao.findByCode в данном случае никогда не будет пустым
        return new ExchangeRateEntity(
                resultSet.getLong("id"),
                CurrencyDao.getInstance().findByCode(resultSet.getString("base_currency_code")).get(),
                CurrencyDao.getInstance().findByCode(resultSet.getString("target_currency_code")).get(),
                resultSet.getBigDecimal("rate")
        );
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }
}
