package org.makson.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String DRIVER = "db.driver";

    static {
        loadDriver();
    }

    private ConnectionManager() {
    }

    private static void loadDriver() {
        try {
            Class.forName(PropertiesUtils.get(DRIVER));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(PropertiesUtils.get(URL_KEY));
    }
}
