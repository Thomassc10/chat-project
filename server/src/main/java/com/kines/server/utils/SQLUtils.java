package com.kines.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.kines.server.database.DatabaseManager;
import com.kines.server.user.User;
import com.zaxxer.hikari.HikariDataSource;

public class SQLUtils {

    private static HikariDataSource dataSource = DatabaseManager.getDataSource();

    public static void insertUser(String email, String username, String password) {
        String sql = "INSERT INTO users_info (id, email, username, password) VALUES(?, ?, ?, ?);";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);) {
            
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, email);
            statement.setString(3, username);
            statement.setString(4, password);
            statement.executeUpdate();
            
            System.out.println("User inserted successfully.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static User getUserByEmail(String email) {
        String sql = "SELECT id, username, email, password FROM users_info;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet set = statement.executeQuery();) {
            
            String e = "";
            while(set.next() || !e.isEmpty()) {
                if (set.getString("email").equalsIgnoreCase(email)) {
                    e = set.getString("email");
                    break;
                }
            }

            if (e.isEmpty()) {
                return null;
            }

            String id = set.getString("id");
            String name = set.getString("username");  
            String password = set.getString("password");
            
            return new User(id, email, name, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static User getUserByUsername(String username) {
        if (username == null) return null;
        String sql = "SELECT id, username, email, password FROM users_info;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql); 
            ResultSet set = statement.executeQuery();) {
            
            String e = "";
            while(set.next() || !e.isEmpty()) {
                if (set.getString("username").equalsIgnoreCase(username)) {
                    e = set.getString("username");
                    break;
                }
            }

            if (e.isEmpty()) {
                return null;
            }

            String id = set.getString("id");
            String email = set.getString("email");
            String password = set.getString("password");
            
            return new User(id, email, username, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static boolean hasEmail(String email) {
        String sql = "SELECT email FROM users_info;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet set = statement.executeQuery();) {
            
            while(set.next()) {
                if (set.getString("email").equalsIgnoreCase(email))
                    return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public static boolean hasUsername(String username) {
        String sql = "SELECT username FROM users_info;";
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet set = statement.executeQuery();) {
            
            while(set.next()) {
                if (set.getString("username").equalsIgnoreCase(sql))
                    return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
