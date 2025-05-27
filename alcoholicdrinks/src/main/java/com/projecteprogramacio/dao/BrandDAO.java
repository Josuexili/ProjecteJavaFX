package com.projecteprogramacio.dao;


import com.projecteprogramacio.model.Brand;
import com.projecteprogramacio.util.Database;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO {

    public void insertBrand(Brand brand) {
        String sql = "INSERT INTO brands (name, country_code) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, brand.getName());
            stmt.setString(2, brand.getCountryCode());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBrand(Brand brand) {
        String sql = "UPDATE brands SET name=?, country_code=? WHERE brand_id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, brand.getName());
            stmt.setString(2, brand.getCountryCode());
            stmt.setInt(3, brand.getBrandId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBrand(int brandId) {
        String sql = "DELETE FROM brands WHERE brand_id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, brandId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Brand getBrandById(int brandId) {
        String sql = "SELECT * FROM brands WHERE brand_id=?";
        Brand brand = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, brandId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                brand = extractBrandFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brand;
    }

    public List<Brand> getAllBrands() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT * FROM brands";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                brands.add(extractBrandFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brands;
    }

    private Brand extractBrandFromResultSet(ResultSet rs) throws SQLException {
        return new Brand(
            rs.getInt("brand_id"),
            rs.getString("name"),
            rs.getString("country_code")
        );
    }
}

