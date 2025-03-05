package org.makson.dao;

import java.util.List;

public interface Dao<T> {
    List<T> findAll();

    T save(T entity);
}
