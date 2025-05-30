package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.DrinkType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO per gestionar l'accés a la taula `drink_types` de la base de
 * dades. Proporciona operacions per inserir o recuperar tipus de beguda.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class DrinkTypeDAO {

	/** Connexió JDBC activa a la base de dades */
	private final Connection conn;

	/**
	 * Constructor que inicialitza el DAO amb la connexió a la base de dades.
	 * 
	 * @param conn connexió JDBC activa
	 */
	public DrinkTypeDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Obté l'identificador d'un tipus de beguda pel seu nom. Si no existeix,
	 * l'insereix i retorna el nou identificador.
	 * 
	 * @param name  nom del tipus de beguda
	 * @param image imatge associada al tipus (pot ser null)
	 * @return l'identificador del tipus existent o nou creat; -1 en cas d'error
	 */
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

			// Insereix nou tipus
			try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
				stmt.setString(1, name);
				stmt.setBytes(2, image);
				int affectedRows = stmt.executeUpdate();

				if (affectedRows > 0) {
					// Recupera l'ID de l'última fila inserida
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
		return -1; // error
	}

	/**
	 * Retorna una llista amb tots els tipus de beguda emmagatzemats.
	 * 
	 * @return llista de tipus de beguda
	 */
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
