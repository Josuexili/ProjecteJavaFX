package com.projecteprogramacio.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.projecteprogramacio.model.Brand;

/**
 * Classe DAO per accedir i manipular les dades de la taula "brands" a la base
 * de dades. Aquesta classe permet obtenir, inserir i consultar marques
 * (brands).
 */
public class BrandDAO {
	/**
	 * Connexió a la base de dades utilitzada per executar les consultes SQL.
	 */
	private final Connection conn;

	/**
	 * Crea un nou BrandDAO amb la connexió a la base de dades especificada.
	 * 
	 * @param conn Connexió JDBC oberta a la base de dades.
	 */
	public BrandDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Retorna l'identificador (brand_id) d'una marca amb el nom indicat. Si no
	 * existeix, crea la marca amb el nom i codi de país indicats i retorna el nou
	 * ID.
	 * 
	 * @param brandName   Nom de la marca.
	 * @param countryCode Codi del país de la marca (ex. "ESP", "USA"). Si és null o
	 *                    buit, s'utilitza "XX" per defecte peruqè no falli.
	 * @return L'identificador de la marca existent o nova, o -1 si hi ha algun
	 *         error.
	 */
	public int getIdOrInsert(String brandName, String countryCode) {
		// Assegura que el countryCode no és null ni buit
		if (countryCode == null || countryCode.trim().isEmpty()) {
			countryCode = "XX"; // codi per defecte
		}

		try {
			// Consulta si la marca ja existeix a la base de dades
			String selectSql = "SELECT brand_id FROM brands WHERE name = ?";
			try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
				stmt.setString(1, brandName);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return rs.getInt("brand_id");
				}
			}

			// Si no existeix, insereix la nova marca
			String insertSql = "INSERT INTO brands (name, country_code) VALUES (?, ?)";
			try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
				stmt.setString(1, brandName);
				stmt.setString(2, countryCode);
				int affectedRows = stmt.executeUpdate();

				if (affectedRows > 0) {
					// Obté l'últim ID inserit (SQLite específic)
					try (Statement stmt2 = conn.createStatement();
							ResultSet rs2 = stmt2.executeQuery("SELECT last_insert_rowid()")) {
						if (rs2.next()) {
							return rs2.getInt(1);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Retorna -1 en cas d'error
		return -1;
	}

	/**
	 * Obté una llista de totes les marques (brands) presents a la base de dades.
	 * 
	 * @return Llista de marques; la llista pot estar buida si no hi ha marques o si
	 *         hi ha un error.
	 */
	public List<Brand> getAllBrands() {
		List<Brand> brands = new ArrayList<>();
		String sql = "SELECT brand_id, name, country_code FROM brands";

		try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Brand brand = new Brand(rs.getInt("brand_id"), rs.getString("name"), rs.getString("country_code"));
				brands.add(brand);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return brands;
	}
}
