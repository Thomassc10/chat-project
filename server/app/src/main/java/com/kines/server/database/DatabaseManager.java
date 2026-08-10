package com.kines.server.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {

    private static HikariDataSource dataSource;

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static void initializePool() {
        HikariConfig config = new HikariConfig();

        config .setJdbcUrl("jdbc:sqlite:users_info.db");
        config.setMaximumPoolSize(1);
        config.setConnectionInitSql("PRAGMA journal_mode=WAL;");

        dataSource = new HikariDataSource(config);
        System.out.println("SQLite connections pool initialized.");

        createTables();
    }

    public static void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS users (id TEXT NOT NULL, email TEXT NOT NULL UNIQUE, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL);";
        try {
            Connection conn = dataSource.getConnection();
            Statement statement = conn.createStatement();
            statement.execute(sql);

            System.out.println("Tables created.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
