package org.makson.dao;

import java.util.List;

public interface Dao<K, T> {
    List<T> findAll();

    void update(T entity);
}
