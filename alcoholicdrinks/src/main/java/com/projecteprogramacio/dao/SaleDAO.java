package com.projecteprogramacio.dao;


import com.projecteprogramacio.model.Sale;
import com.projecteprogramacio.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    // Inserir nova venda
    public boolean insertSale(Sale sale) {
        String sql = "INSERT INTO sales (ticket_id, total_amount, created_at, cumulative_total) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sale.getTicketId());
            stmt.setDouble(2, sale.getTotalAmount());
            stmt.setString(3, sale.getCreatedAt());
            stmt.setDouble(4, sale.getCumulativeTotal());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtenir totes les vendes
    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT sale_id, ticket_id, total_amount, created_at, cumulative_total FROM sales";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Sale sale = new Sale(
                    rs.getInt("sale_id"),
                    rs.getInt("ticket_id"),
                    rs.getDouble("total_amount"),
                    rs.getString("created_at"),
                    rs.getDouble("cumulative_total")
                );
                sales.add(sale);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }

    // Obtenir una venda per ID
    public Sale getSaleById(int id) {
        String sql = "SELECT sale_id, ticket_id, total_amount, created_at, cumulative_total FROM sales WHERE sale_id = ?";
        Sale sale = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                sale = new Sale(
                    rs.getInt("sale_id"),
                    rs.getInt("ticket_id"),
                    rs.getDouble("total_amount"),
                    rs.getString("created_at"),
                    rs.getDouble("cumulative_total")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sale;
    }

    // Actualitzar una venda
    public boolean updateSale(Sale sale) {
        String sql = "UPDATE sales SET ticket_id = ?, total_amount = ?, created_at = ?, cumulative_total = ? WHERE sale_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sale.getTicketId());
            stmt.setDouble(2, sale.getTotalAmount());
            stmt.setString(3, sale.getCreatedAt());
            stmt.setDouble(4, sale.getCumulativeTotal());
            stmt.setInt(5, sale.getSaleId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar una venda
    public boolean deleteSale(int saleId) {
        String sql = "DELETE FROM sales WHERE sale_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
