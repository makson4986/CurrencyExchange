package org.makson.dao;

import org.makson.entities.CurrencyEntity;
import org.makson.exception.CurrencyAlreadyExistException;
import org.makson.exception.CurrencyNotFoundException;
import org.makson.utils.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao implements Dao<CurrencyEntity> {
    private static final CurrencyDao INSTANCE = new CurrencyDao();
    private final String FIND_ALL = """
            SELECT id, code, full_name, sign
            FROM currencies
            """;
    private final String INSERT = """
            INSERT INTO currencies(code, full_name, sign)
            VALUES (?, ?, ?)
            """;
    private final String FIND_BY_CODE = FIND_ALL + "WHERE code = ?";

    private CurrencyDao() {
    }

    public CurrencyEntity findByCode(String code) throws CurrencyNotFoundException, SQLException {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_CODE)) {
            prepareStatement.setString(1, code);
            ResultSet resultSet = prepareStatement.executeQuery();

            if (resultSet.next()) {
                return buildCurrency(resultSet);
            }

            throw new CurrencyNotFoundException();
        }
    }

    @Override
    public List<CurrencyEntity> findAll() throws SQLException {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = prepareStatement.executeQuery();
            List<CurrencyEntity> currencies = new ArrayList<>();

            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }

            return currencies;
        }
    }

    @Override
    public CurrencyEntity save(CurrencyEntity entity) throws CurrencyAlreadyExistException, SQLException {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setString(1, entity.getCode());
            prepareStatement.setString(2, entity.getFullName());
            prepareStatement.setString(3, entity.getSign());

            prepareStatement.executeUpdate();

            try (var generatedKeys = prepareStatement.getGeneratedKeys()) {
                generatedKeys.next();
                entity.setId(generatedKeys.getLong(1));
                return entity;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new CurrencyAlreadyExistException();
            }
            throw new SQLException(e);
        }
    }

    @Override
    public CurrencyEntity update(CurrencyEntity entity) {
        return null;
    }

    private CurrencyEntity buildCurrency(ResultSet resultSet) throws SQLException {
        return new CurrencyEntity(
                resultSet.getLong("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }
}
