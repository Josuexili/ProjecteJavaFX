package com.projecteprogramacio.dao;


import com.projecteprogramacio.model.DrinkType;
import com.projecteprogramacio.util.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DrinkTypeDAO {

    // Crear un nou tipus de beguda
    public boolean insertDrinkType(DrinkType drinkType) {
        String sql = "INSERT INTO drink_types (name, image) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, drinkType.getName());
            stmt.setBytes(2, drinkType.getImage());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtenir tots els tipus
    public List<DrinkType> getAllDrinkTypes() {
        List<DrinkType> list = new ArrayList<>();
        String sql = "SELECT type_id, name, image FROM drink_types";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                DrinkType dt = new DrinkType(
                    rs.getInt("type_id"),
                    rs.getString("name"),
                    rs.getBytes("image")
                );
                list.add(dt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Obtenir un tipus pel seu ID
    public DrinkType getDrinkTypeById(int id) {
        String sql = "SELECT type_id, name, image FROM drink_types WHERE type_id = ?";
        DrinkType drinkType = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                drinkType = new DrinkType(
                    rs.getInt("type_id"),
                    rs.getString("name"),
                    rs.getBytes("image")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drinkType;
    }

    // Actualitzar un tipus de beguda
    public boolean updateDrinkType(DrinkType drinkType) {
        String sql = "UPDATE drink_types SET name = ?, image = ? WHERE type_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, drinkType.getName());
            stmt.setBytes(2, drinkType.getImage());
            stmt.setInt(3, drinkType.getTypeId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar un tipus de beguda
    public boolean deleteDrinkType(int id) {
        String sql = "DELETE FROM drink_types WHERE type_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

