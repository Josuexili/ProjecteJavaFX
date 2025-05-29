package com.projecteprogramacio.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.projecteprogramacio.model.Brand;

public class BrandDAO {
    private final Connection conn;

    public BrandDAO(Connection conn) {
        this.conn = conn;
    }
    
    

    public int getIdOrInsert(String brandName, String countryCode) {
        // Assegura countryCode no null ni buit
        if (countryCode == null || countryCode.trim().isEmpty()) {
            countryCode = "XX";  // codi per defecte
        }

        try {
            // Comprova si la marca ja existeix
            String selectSql = "SELECT brand_id FROM brands WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                stmt.setString(1, brandName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("brand_id");
                }
            }

            // Insereix la nova marca amb country_code vàlid
            String insertSql = "INSERT INTO brands (name, country_code) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setString(1, brandName);
                stmt.setString(2, countryCode);
                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
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





    public List<Brand> getAllBrands() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT brand_id, name, country_code FROM brands";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Brand brand = new Brand(
                    rs.getInt("brand_id"),
                    rs.getString("name"),
                    rs.getString("country_code")
                );
                brands.add(brand);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brands;
    }
    
}

