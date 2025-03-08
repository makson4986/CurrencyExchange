package org.makson.dao;

import org.makson.entities.ExchangeRateEntity;
import org.makson.exception.CurrencyNotFoundException;
import org.makson.exception.ExchangeRateNotFoundException;
import org.makson.utils.ConnectionManager;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements Dao<ExchangeRateEntity> {
    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
    private final String FIND_ALL = """
            SELECT
                er.id,
                c1.code AS base_currency_code,
                c2.code AS target_currency_code,
                er.rate
            FROM exchange_rates er
            JOIN currencies c1 ON er.base_currency_id = c1.id
            JOIN currencies c2 ON er.target_currency_id = c2.id
            """;

    private final String INSERT = """
            INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?)
            """;

    private final String FIND_BY_EXCHANGE_RATE = FIND_ALL + "WHERE c1.code = ? and c2.code = ?";
    private final String UPDATE = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE base_currency_id = (
                SELECT id
                FROM currencies
                WHERE code = ?
            )
            AND target_currency_id = (
                SELECT id
                FROM currencies
                WHERE code = ?
            )
            """;

    private ExchangeRateDao() {

    }

    public Optional<ExchangeRateEntity> findByExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        ExchangeRateEntity exchangeRateEntity = null;


        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_EXCHANGE_RATE)) {
            prepareStatement.setString(1, baseCurrencyCode);
            prepareStatement.setString(2, targetCurrencyCode);

            ResultSet resultSet = prepareStatement.executeQuery();

            if (resultSet.next()) {
                exchangeRateEntity = buildExchangeRate(resultSet);
            }

            return Optional.ofNullable(exchangeRateEntity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
    public Optional<ExchangeRateEntity> save(ExchangeRateEntity entity) {
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

            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExchangeRateEntity> update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(UPDATE)) {
            prepareStatement.setBigDecimal(1, rate);
            prepareStatement.setString(2, baseCurrencyCode);
            prepareStatement.setString(3, targetCurrencyCode);

            if (prepareStatement.executeUpdate() > 0) {
                return findByExchangeRate(baseCurrencyCode, targetCurrencyCode);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ExchangeRateEntity update(BigDecimal rate) {

        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL)) {
            prepareStatement.setBigDecimal(1, rate);
            prepareStatement.executeUpdate();

            try (var generatedKeys = prepareStatement.getGeneratedKeys()) {
                generatedKeys.next();
                return buildExchangeRate(generatedKeys);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ExchangeRateEntity buildExchangeRate(ResultSet resultSet) throws SQLException {
        //CurrencyDao.findByCode в данном методе всегда возвращает не пустой optional
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
