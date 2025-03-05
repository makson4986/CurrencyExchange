package org.makson.dao;

import org.makson.entity.CurrencyEntity;
import org.makson.utils.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Long, CurrencyEntity> {
    private static final CurrencyDao INSTANCE = new CurrencyDao();
    private static final String FIND_ALL = """
            SELECT id, code, full_name, sign
            FROM currencies
            """;
    private static final String INSERT = """
            INSERT INTO currencies(code, full_name, sign)
            VALUES (?, ?, ?)
            """;
    private static final String FIND_BY_CODE = FIND_ALL + "WHERE code = ?";

    private CurrencyDao() {
    }

    public Optional<CurrencyEntity> findByCode(String code) {
        CurrencyEntity currency = null;

        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_CODE)) {
            prepareStatement.setString(1, code);
            ResultSet resultSet = prepareStatement.executeQuery();

            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }

            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CurrencyEntity> findAll() {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = prepareStatement.executeQuery();
            List<CurrencyEntity> currencies = new ArrayList<>();

            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }

            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CurrencyEntity save(CurrencyEntity entity) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setString(1, entity.getCode());
            prepareStatement.setString(2, entity.getFullName());
            prepareStatement.setString(3, entity.getSign());

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

    private CurrencyEntity buildCurrency(ResultSet resultSet) {
        try {
            return new CurrencyEntity(
                    resultSet.getLong("id"),
                    resultSet.getString("code"),
                    resultSet.getString("full_name"),
                    resultSet.getString("sign")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }
}
