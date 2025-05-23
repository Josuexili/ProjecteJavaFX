package com.projecteprogramacio.dao;


import com.projecteprogramacio.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    // Crear un nou usuari
    public boolean createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, created_at, last_login, last_logout, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getCreatedAt());
            pstmt.setString(5, user.getLastLogin());
            pstmt.setString(6, user.getLastLogout());
            pstmt.setString(7, user.getRole());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Obtenir un usuari per ID
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        }
    }

    // Obtenir tots els usuaris
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    // Actualitzar un usuari existent
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, created_at = ?, last_login = ?, last_logout = ?, role = ? " +
                     "WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getCreatedAt());
            pstmt.setString(5, user.getLastLogin());
            pstmt.setString(6, user.getLastLogout());
            pstmt.setString(7, user.getRole());
            pstmt.setInt(8, user.getUserId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Eliminar un usuari per ID
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Mètode auxiliar per convertir ResultSet en User
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email"),
            rs.getString("created_at"),
            rs.getString("last_login"),
            rs.getString("last_logout"),
            rs.getString("role")
        );
    }
}