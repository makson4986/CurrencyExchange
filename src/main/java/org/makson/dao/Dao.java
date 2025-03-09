package org.makson.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    List<T> findAll() throws SQLException;

    T save(T entity) throws Exception;

    T update(T entity) throws Exception;
}
