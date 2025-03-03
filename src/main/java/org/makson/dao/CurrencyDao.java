package org.makson.dao;

import org.makson.entity.CurrencyEntity;
import org.makson.utils.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao implements Dao<Long, CurrencyEntity> {
    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private CurrencyDao() {
    }

    @Override
    public List<CurrencyEntity> findAll() {
        final String sql = """
                SELECT id, code, full_name, sign
                FROM currencies
                """;

        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(sql)) {
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
    public void update(CurrencyEntity entity) {

    }

    @Override
    public CurrencyEntity save(CurrencyEntity entity) {
        final String sql = """
                INSERT INTO currencies(code, full_name, sign)
                VALUES (?, ?, ?)
                """;

        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
