package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO (Data Access Object) per gestionar les operacions sobre la taula `users` a la base de dades.
 * Proporciona mètodes per crear, llegir, actualitzar i eliminar usuaris.
 * La connexió a la base de dades s'ha de passar al constructor.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class UserDAO {

    /** Connexió a la base de dades que s'utilitza per a les operacions. */
    private Connection connection;

    /**
     * Constructor que rep una connexió activa a la base de dades.
     * 
     * @param connection Connexió JDBC a la base de dades.
     */
    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Crea un nou usuari a la base de dades.
     * 
     * @param user Objecte User amb la informació a inserir.
     * @return true si l'usuari s'ha creat correctament, false en cas contrari.
     * @throws SQLException En cas d'errors en la consulta SQL.
     */
    public boolean createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, created_at, last_login, last_logout, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getCreatedAt());
            pstmt.setString(5, user.getLastLogin());
            pstmt.setString(6, user.getLastLogout());
            pstmt.setString(7, user.getRole());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            // Obtenir l'ID generat i assignar-lo a l'objecte User
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
            }

            return true;
        }
    }

    /**
     * Obté un usuari a partir del seu identificador únic.
     * 
     * @param userId Identificador de l'usuari.
     * @return Objecte User amb les dades de l'usuari, o null si no existeix.
     * @throws SQLException En cas d'errors en la consulta SQL.
     */
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

    /**
     * Obté un usuari a partir del seu nom d'usuari.
     * Molt útil per a la funcionalitat de login.
     * 
     * @param username Nom d'usuari.
     * @return Objecte User amb les dades de l'usuari, o null si no existeix.
     * @throws SQLException En cas d'errors en la consulta SQL.
     */
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        }
    }

    /**
     * Obté tots els usuaris registrats a la base de dades.
     * 
     * @return Llista d'usuaris.
     * @throws SQLException En cas d'errors en la consulta SQL.
     */
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

    /**
     * Actualitza la informació d'un usuari existent a la base de dades.
     * 
     * @param user Objecte User amb la informació actualitzada.
     * @return true si l'usuari s'ha actualitzat correctament, false en cas contrari.
     * @throws SQLException En cas d'errors en la consulta SQL.
     */
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

    /**
     * Elimina un usuari de la base de dades a partir del seu identificador.
     * 
     * @param userId Identificador de l'usuari a eliminar.
     * @return true si l'usuari s'ha eliminat correctament, false en cas contrari.
     * @throws SQLException En cas d'errors en la consulta SQL.
     */
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Cerca usuaris pel nom, fent servir un filtre amb LIKE SQL.
     * 
     * @param nameFilter Text que ha de contenir el nom d'usuari.
     * @return Llista d'usuaris que coincideixen amb el filtre.
     * @throws SQLException En cas d'errors en la consulta SQL.
     */
    public List<User> searchUsersByName(String nameFilter) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nameFilter + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    /**
     * Mètode auxiliar privat que transforma un ResultSet en un objecte User.
     * 
     * @param rs ResultSet posicionat en una fila vàlida.
     * @return Objecte User amb les dades del ResultSet.
     * @throws SQLException En cas d'errors en l'accés a les dades del ResultSet.
     */
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

