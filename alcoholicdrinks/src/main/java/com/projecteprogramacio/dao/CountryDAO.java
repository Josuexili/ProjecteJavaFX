package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.Country;
import com.projecteprogramacio.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO per gestionar l'accés a la taula `countries` de la base de dades.
 * Proporciona mètodes per obtenir o inserir països i per obtenir la llista completa de països.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class CountryDAO {
    
    /** Connexió JDBC activa a la base de dades. */
    private final Connection conn;

    /**
     * Constructor que rep la connexió a la base de dades.
     * 
     * @param conn Connexió JDBC.
     */
    public CountryDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Comprova si un país amb el codi especificat existeix i, si no, l'insereix.
     * 
     * @param countryCode Codi del país (p. ex. "ES").
     * @param countryName Nom del país (p. ex. "Spain").
     * @return true si el país ja existia o s'ha inserit correctament; false en cas d'error.
     */
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

    /**
     * Obté la llista completa de països de la base de dades.
     * 
     * @return Llista d'objectes Country amb tots els països.
     */
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
