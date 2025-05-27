package com.projecteprogramacio.dao;


import com.projecteprogramacio.model.TicketItem;
import com.projecteprogramacio.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketItemDAO {

    public void insertTicketItem(TicketItem item) {
        String sql = "INSERT INTO ticket_items (ticket_id, drink_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getTicketId());
            stmt.setInt(2, item.getDrinkId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getPrice());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTicketItem(TicketItem item) {
        String sql = "UPDATE ticket_items SET ticket_id=?, drink_id=?, quantity=?, price=? WHERE item_id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getTicketId());
            stmt.setInt(2, item.getDrinkId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getPrice());
            stmt.setInt(5, item.getItemId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTicketItem(int itemId) {
        String sql = "DELETE FROM ticket_items WHERE item_id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TicketItem getTicketItemById(int itemId) {
        String sql = "SELECT * FROM ticket_items WHERE item_id=?";
        TicketItem item = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                item = extractItemFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return item;
    }

    public List<TicketItem> getAllTicketItems() {
        List<TicketItem> items = new ArrayList<>();
        String sql = "SELECT * FROM ticket_items";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    private TicketItem extractItemFromResultSet(ResultSet rs) throws SQLException {
        return new TicketItem(
            rs.getInt("item_id"),
            rs.getInt("ticket_id"),
            rs.getInt("drink_id"),
            rs.getInt("quantity"),
            rs.getDouble("price")
        );
    }
}
