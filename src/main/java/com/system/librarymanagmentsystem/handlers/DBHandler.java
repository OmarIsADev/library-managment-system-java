package com.system.librarymanagmentsystem.handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

abstract public class DBHandler {

    private static final String URL = "jdbc:sqlite:library.db";
    private Connection connection = null;

    protected void connect() {
        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    protected void createTable(String createTableSql) {
        if (connection == null) {
            connect();
        }
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql);
            System.out.println("Table created successfully (if it didn't already exist).");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    abstract public <T> void insertRecord(T record);

    abstract public <T> T getRecordById(int id);

    abstract public <T> T[] getAllRecords();

    abstract public <T> void updateRecord(T record);

    abstract public <T> void deleteRecord(T record);

    public Connection getConnection() {
        return this.connection;
    }
}
