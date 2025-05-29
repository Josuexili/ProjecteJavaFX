package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.Drink;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO per gestionar l'accés a la taula `drinks` de la base de dades.
 * Proporciona operacions CRUD i funcionalitats de cerca i obtenció de begudes.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class DrinkDAO {

    /** Connexió JDBC activa a la base de dades */
    private final Connection conn;

    /**
     * Constructor que inicialitza el DAO amb la connexió a la base de dades.
     * 
     * @param conn connexió JDBC activa
     */
    public DrinkDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Insereix una nova beguda a la base de dades.
     * 
     * @param drink objecte Drink a inserir
     * @return true si la inserció ha tingut èxit; false en cas contrari
     */
    public boolean insertDrink(Drink drink) {
        String sql = "INSERT INTO drinks (name, type_id, brand_id, country_code, alcohol_content, " +
                     "description, volume, price, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, drink.getName());
            stmt.setInt(2, drink.getTypeId());
            stmt.setInt(3, drink.getBrandId());
            stmt.setString(4, drink.getCountryCode());
            stmt.setDouble(5, drink.getAlcoholContent());
            stmt.setString(6, drink.getDescription());
            stmt.setDouble(7, drink.getVolume());
            stmt.setDouble(8, drink.getPrice());
            stmt.setBytes(9, drink.getImage());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting drink: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualitza les dades d'una beguda existent a la base de dades.
     * 
     * @param drink objecte Drink amb les dades actualitzades (ha de tenir l'id correcte)
     * @return true si l'actualització ha tingut èxit; false en cas contrari
     */
    public boolean updateDrink(Drink drink) {
        String sql = "UPDATE drinks SET name=?, type_id=?, brand_id=?, country_code=?, alcohol_content=?, " +
                     "description=?, volume=?, price=?, image=? WHERE drink_id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, drink.getName());
            stmt.setInt(2, drink.getTypeId());
            stmt.setInt(3, drink.getBrandId());
            stmt.setString(4, drink.getCountryCode());
            stmt.setDouble(5, drink.getAlcoholContent());
            stmt.setString(6, drink.getDescription());
            stmt.setDouble(7, drink.getVolume());
            stmt.setDouble(8, drink.getPrice());
            stmt.setBytes(9, drink.getImage());
            stmt.setInt(10, drink.getDrinkId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating drink: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una beguda de la base de dades segons el seu identificador.
     * 
     * @param drinkId identificador de la beguda a eliminar
     * @return true si l'eliminació ha tingut èxit; false en cas contrari
     */
    public boolean deleteDrink(int drinkId) {
        String sql = "DELETE FROM drinks WHERE drink_id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, drinkId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting drink: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obté una beguda segons el seu identificador.
     * 
     * @param drinkId identificador de la beguda
     * @return objecte Drink si es troba; null en cas contrari
     */
    public Drink getDrinkById(int drinkId) {
        String sql = "SELECT d.drink_id, d.name, d.type_id, d.brand_id, d.country_code, d.alcohol_content, " +
                     "d.description, d.volume, d.price, d.image, " +
                     "b.name AS brandName, c.name AS countryName " +
                     "FROM drinks d " +
                     "LEFT JOIN brands b ON d.brand_id = b.brand_id " +
                     "LEFT JOIN countries c ON d.country_code = c.country_code " +
                     "WHERE d.drink_id = ?";

        Drink drink = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, drinkId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    drink = extractDrinkFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving drink by ID: " + e.getMessage());
        }

        return drink;
    }

    /**
     * Obté totes les begudes disponibles a la base de dades.
     * 
     * @return llista de tots els objectes Drink
     */
    public List<Drink> getAllDrinks() {
        List<Drink> drinks = new ArrayList<>();
        String sql = "SELECT d.drink_id, d.name, d.type_id, d.brand_id, d.country_code, d.alcohol_content, " +
                     "d.description, d.volume, d.price, d.image, " +
                     "b.name AS brandName, c.name AS countryName " +
                     "FROM drinks d " +
                     "LEFT JOIN brands b ON d.brand_id = b.brand_id " +
                     "LEFT JOIN countries c ON d.country_code = c.country_code";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                drinks.add(extractDrinkFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all drinks: " + e.getMessage());
        }

        return drinks;
    }

    /**
     * Cerca begudes pel seu nom que continguin un filtre de text.
     * 
     * @param nameFilter text a cercar dins el nom de la beguda
     * @return llista de begudes que coincideixen amb el filtre; pot ser buida si no hi ha coincidències
     */
    public List<Drink> searchDrinksByName(String nameFilter) {
        List<Drink> drinks = new ArrayList<>();
        String sql = "SELECT d.drink_id, d.name, d.type_id, d.brand_id, d.country_code, d.alcohol_content, " +
                     "d.description, d.volume, d.price, d.image, " +
                     "b.name AS brandName, c.name AS countryName " +
                     "FROM drinks d " +
                     "LEFT JOIN brands b ON d.brand_id = b.brand_id " +
                     "LEFT JOIN countries c ON d.country_code = c.country_code " +
                     "WHERE d.name LIKE ?";

        if (nameFilter == null || nameFilter.trim().isEmpty()) {
            return drinks;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nameFilter + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    drinks.add(extractDrinkFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching drinks by name: " + e.getMessage());
        }

        return drinks;
    }

    /**
     * Mètode auxiliar per extreure un objecte Drink del ResultSet.
     * 
     * @param rs ResultSet posicionat a la fila actual
     * @return objecte Drink amb les dades extretes
     * @throws SQLException en cas d'error d'accés al ResultSet
     */
    private Drink extractDrinkFromResultSet(ResultSet rs) throws SQLException {
        Drink drink = new Drink(
            rs.getInt("drink_id"),
            rs.getString("name"),
            rs.getInt("type_id"),
            rs.getInt("brand_id"),
            rs.getString("country_code"),
            rs.getDouble("alcohol_content"),
            rs.getString("description"),
            rs.getDouble("volume"),
            rs.getDouble("price"),
            rs.getBytes("image"),
            rs.getString("brandName"),
            rs.getString("countryName")
        );
        return drink;
    }
}

