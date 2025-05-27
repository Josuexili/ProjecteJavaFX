package com.projecteprogramacio.dao;


import com.projecteprogramacio.model.Drink;
import com.projecteprogramacio.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DrinkDAO {

	public static boolean insertDrink(Drink drink) {
	    String sql = "INSERT INTO drinks (name, type_id, brand_id, country_code, alcohol_content, " +
	                 "description, volume, price, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, drink.getName());
	        stmt.setInt(2, drink.getTypeId());
	        stmt.setInt(3, drink.getBrandId());
	        stmt.setString(4, drink.getCountryCode());
	        stmt.setDouble(5, drink.getAlcoholContent());
	        stmt.setString(6, drink.getDescription());
	        stmt.setDouble(7, drink.getVolume());
	        stmt.setDouble(8, drink.getPrice());
	        stmt.setBytes(9, drink.getImage());

	        int rows = stmt.executeUpdate();
	        return rows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

    public static void updateDrink(Drink drink) {
        String sql = "UPDATE drinks SET name=?, type_id=?, brand_id=?, country_code=?, alcohol_content=?, " +
                     "description=?, volume=?, price=?, image=? WHERE drink_id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDrink(int drinkId) {
        String sql = "DELETE FROM drinks WHERE drink_id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, drinkId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Drink getDrinkById(int drinkId) {
        String sql = "SELECT * FROM drinks WHERE drink_id=?";
        Drink drink = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, drinkId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                drink = extractDrinkFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drink;
    }

    public static List<Drink> getAllDrinks() {
        List<Drink> drinks = new ArrayList<>();
        String sql = "SELECT * FROM drinks";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                drinks.add(extractDrinkFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drinks;
    }

    private static Drink extractDrinkFromResultSet(ResultSet rs) throws SQLException {
        return new Drink(
            rs.getInt("drink_id"),
            rs.getString("name"),
            rs.getInt("type_id"),
            rs.getInt("brand_id"),
            rs.getString("country_code"),
            rs.getDouble("alcohol_content"),
            rs.getString("description"),
            rs.getDouble("volume"),
            rs.getDouble("price"),
            rs.getBytes("image")
        );
    }
    public static List<Drink> searchDrinksByName(String nameFilter) {
        List<Drink> drinks = new ArrayList<>();
        String sql = "SELECT * FROM drinks WHERE name LIKE ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nameFilter + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                drinks.add(extractDrinkFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drinks;
    }

}
