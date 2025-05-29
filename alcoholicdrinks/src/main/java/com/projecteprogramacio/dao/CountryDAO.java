package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.Country;
import com.projecteprogramacio.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CountryDAO {
	private final Connection conn;

	public CountryDAO(Connection conn) {
		this.conn = conn;
	}

	public boolean getOrInsert(String countryCode, String countryName) {
		String selectSql = "SELECT country_code FROM countries WHERE country_code = ?";
		String insertSql = "INSERT INTO countries (country_code, name) VALUES (?, ?)";

		try (Connection conn = Database.getConnection()) {
			// Comprova si ja existeix
			try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
				stmt.setString(1, countryCode);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return true; // Ja existeix
				}
			}

			// Inserta nou país
			try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
				stmt.setString(1, countryCode);
				stmt.setString(2, countryName);
				return stmt.executeUpdate() > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Country> getAllCountries() {
		List<Country> list = new ArrayList<>();
		String sql = "SELECT country_code, name FROM countries";

		try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Country country = new Country(rs.getString("country_code"), rs.getString("name"));
				list.add(country);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

}
