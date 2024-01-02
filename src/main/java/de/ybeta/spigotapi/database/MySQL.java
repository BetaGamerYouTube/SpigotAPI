package de.ybeta.spigotapi.database;

import java.sql.*;

public class MySQL implements Database {

    private Connection connection;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final boolean autoReconnect;
    private boolean cancel = false;

    public MySQL(String host, int port, String database, String username, String password, boolean autoReconnect) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.autoReconnect = autoReconnect;
    }

    @Override
    public void connect() {
        if (isConnected()) return;
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + ":" + this.port + "/" + database + "?autoReconnect=" + this.autoReconnect,
                    this.username, this.password);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        if (!isConnected()) return;
        try {
            this.connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void update(String query, Object... parameters) {
        if (!isConnected()) return;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            preparedStatement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
            if (cancel) return;
            cancel = true;
            connect();
            update(query, parameters);
        }
    }

    @Override
    public ResultSet getResult(String query, Object... parameters) {
        if (!isConnected()) return null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            return preparedStatement.executeQuery();
        } catch (SQLException exception) {
            exception.printStackTrace();
            if (cancel) return null;
            cancel = true;
            connect();
            return getResult(query, parameters);
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public boolean isConnected() {
        return this.connection != null;
    }
}
