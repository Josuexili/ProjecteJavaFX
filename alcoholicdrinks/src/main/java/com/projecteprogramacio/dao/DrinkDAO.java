package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.Drink;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DrinkDAO {

    private final Connection conn;

    public DrinkDAO(Connection conn) {
        this.conn = conn;
    }

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

    public Drink getDrinkById(int drinkId) {
        String sql = "SELECT d.drink_id, d.name, d.type_id, d.brand_id, d.country_code, d.alcohol_content, " +
                     "d.description, d.volume, d.price, d.image, " +
                     "b.name AS brandName, c.name AS countryName " +
                     "FROM drinks d " +
                     "LEFT JOIN brands b ON d.brand_id = b.brand_id " +
                     "LEFT JOIN countries c ON d.country_code = c.code " +
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

    public List<Drink> getAllDrinks() {
        List<Drink> drinks = new ArrayList<>();
        String sql = "SELECT d.drink_id, d.name, d.type_id, d.brand_id, d.country_code, d.alcohol_content, " +
                     "d.description, d.volume, d.price, d.image, " +
                     "b.name AS brandName, c.name AS countryName " +
                     "FROM drinks d " +
                     "LEFT JOIN brands b ON d.brand_id = b.brand_id " +
                     "LEFT JOIN countries c ON d.country_code = c.code";

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

    public List<Drink> searchDrinksByName(String nameFilter) {
        List<Drink> drinks = new ArrayList<>();
        String sql = "SELECT d.drink_id, d.name, d.type_id, d.brand_id, d.country_code, d.alcohol_content, " +
                     "d.description, d.volume, d.price, d.image, " +
                     "b.name AS brandName, c.name AS countryName " +
                     "FROM drinks d " +
                     "LEFT JOIN brands b ON d.brand_id = b.brand_id " +
                     "LEFT JOIN countries c ON d.country_code = c.code " +
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
            rs.getString("brandName"),    // Nom marca
            rs.getString("countryName")   // Nom país
        );
        return drink;
    }
}

