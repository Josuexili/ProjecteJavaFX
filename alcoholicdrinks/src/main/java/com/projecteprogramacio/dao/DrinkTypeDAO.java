package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.DrinkType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DrinkTypeDAO {

	private final Connection conn;

	public DrinkTypeDAO(Connection conn) {
		this.conn = conn;
	}

	// Inserta només si no existeix
	public int getOrInsert(String name, byte[] image) {
		String selectSql = "SELECT type_id FROM drink_types WHERE name = ?";
		String insertSql = "INSERT INTO drink_types (name, image) VALUES (?, ?)";

		try {
			// Comprova si ja existeix
			try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
				stmt.setString(1, name);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return rs.getInt("type_id"); // Ja existeix
				}
			}

			// Inserta nou tipus
			try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, name);
				stmt.setBytes(2, image);
				int affectedRows = stmt.executeUpdate();

				if (affectedRows > 0) {
					ResultSet generatedKeys = stmt.getGeneratedKeys();
					if (generatedKeys.next()) {
						return generatedKeys.getInt(1); // Retorna l'ID generat
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1; // Error
	}

	public List<DrinkType> getAllDrinkTypes() {
		List<DrinkType> list = new ArrayList<>();
		String sql = "SELECT type_id, name, image FROM drink_types";

		try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				DrinkType drinkType = new DrinkType(rs.getInt("type_id"), rs.getString("name"), rs.getBytes("image"));
				list.add(drinkType);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
}
