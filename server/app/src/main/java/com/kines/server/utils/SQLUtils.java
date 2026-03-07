package com.kines.server.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import com.kines.server.user.User;

public class SQLUtils {

    private static final String JDBC_URL = "jdbc:sqlite:usersinfo.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (id TEXT NOT NULL, email TEXT NOT NULL UNIQUE, name TEXT NOT NULL, password TEXT NOT NULL);";
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            statement.execute(sql);
            System.out.println("Table created.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void insertUser(String email, String name, String password) {
        String sql = "INSERT INTO users (id, email, name, password) VALUES(?, ?, ?, ?);";
        try {
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, email);
            statement.setString(3, name);
            statement.setString(4, password);
            statement.executeUpdate();
            System.out.println("User inserted successfully.");
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void readUsers() {
        String sql = "SELECT id, name, email, password FROM users;";
        try {
            Connection conn = getConnection();
            var statement = conn.createStatement();
            ResultSet set = statement.executeQuery(sql);
            while(set.next()) {
                String id = set.getString("id");
                String name = set.getString("name");  
                String email = set.getString("email");
                String password = set.getString("password");
                System.out.printf("ID: %s, Name: %s, Email: %s%n, Password: %s\n", id, name, email, password); 
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error reading users: " + e.getMessage());
        }
    }

    public static User getUserByID(String id) {
        String sql = "SELECT id, name, email, password FROM users;";
        try {
            Connection conn = getConnection();
            var statement = conn.createStatement();
            ResultSet set = statement.executeQuery(sql);
            String uuid = null;
            while(set.next() || uuid == null) {
                if (set.getString("id").equals(id))
                    uuid = set.getString("id");
            }

            if (uuid.isEmpty()) {
                return null;
            }

            String name = set.getString("name");  
            String email = set.getString("email");
            String password = set.getString("password");
            conn.close();
            return new User(uuid, email, name, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static User getUserByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM users;";
        try {
            Connection conn = getConnection();
            var statement = conn.createStatement();
            ResultSet set = statement.executeQuery(sql);
            String e = "";
            while(set.next() || e.isEmpty()) {
                if (set.getString("email").equals(email)) {
                    e = set.getString("email");
                    break;
                }
            }

            if (e.isEmpty()) {
                return null;
            }

            String id = set.getString("id");
            String name = set.getString("name");  
            String password = set.getString("password");
            conn.close();
            return new User(id, email, name, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static boolean hasEmail(String email) {
        String sql = "SELECT email FROM users;";
        try {
            Connection conn = getConnection();
            var statement = conn.createStatement();
            ResultSet set = statement.executeQuery(sql);
            while(set.next()) {
                if (set.getString("email").equals(email))
                    return true;
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}
