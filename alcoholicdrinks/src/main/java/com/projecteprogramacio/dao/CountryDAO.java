package com.projecteprogramacio.dao;


import com.projecteprogramacio.model.Country;
import com.projecteprogramacio.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CountryDAO {

    // Afegir un país
    public boolean insertCountry(Country country) {
        String sql = "INSERT INTO countries (country_code, name) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, country.getCountryCode());
            stmt.setString(2, country.getName());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtenir tots els països
    public List<Country> getAllCountries() {
        List<Country> countries = new ArrayList<>();
        String sql = "SELECT country_code, name FROM countries";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Country country = new Country();
                country.setCountryCode(rs.getString("country_code"));
                country.setName(rs.getString("name"));
                countries.add(country);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return countries;
    }

    // Buscar un país pel seu codi
    public Country getCountryByCode(String code) {
        String sql = "SELECT country_code, name FROM countries WHERE country_code = ?";
        Country country = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                country = new Country(rs.getString("country_code"), rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return country;
    }

    // Actualitzar país
    public boolean updateCountry(Country country) {
        String sql = "UPDATE countries SET name = ? WHERE country_code = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, country.getName());
            stmt.setString(2, country.getCountryCode());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar país
    public boolean deleteCountry(String code) {
        String sql = "DELETE FROM countries WHERE country_code = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
