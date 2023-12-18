package de.ybeta.spigotapi.database;

import java.sql.Connection;
import java.sql.ResultSet;

public interface Database {

    void connect();
    void disconnect();
    void update(String query, Object... parameters);
    ResultSet getResult(String query, Object... parameters);
    Connection getConnection();
    boolean isConnected();

}
