package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.Drink;
import com.projecteprogramacio.model.TicketLine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketLineDAO {

    private final Connection conn;
    private final DrinkDAO drinkDAO;

    public TicketLineDAO(Connection conn) {
        this.conn = conn;
        this.drinkDAO = new DrinkDAO(conn);
    }

    /**
     * Obté totes les línies d’un tiquet donat el seu ID.
     */
    public List<TicketLine> getLinesByTicketId(int ticketId) {
        List<TicketLine> lines = new ArrayList<>();
        String sql = "SELECT * FROM ticket_lines WHERE ticket_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int drinkId = rs.getInt("drink_id");
                    Drink drink = drinkDAO.getDrinkById(drinkId);

                    if (drink != null) {
                        TicketLine line = new TicketLine(
                                rs.getInt("ticket_line_id"),
                                rs.getInt("ticket_id"),
                                drink,
                                rs.getInt("quantity")
                        );
                        lines.add(line);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obtenint les línies del tiquet: " + e.getMessage());
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * Insereix una línia de tiquet a la base de dades.
     */
    public boolean insertLine(TicketLine line) throws SQLException {
        String sql = "INSERT INTO ticket_lines (ticket_id, drink_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, line.getTicketId());
            stmt.setInt(2, line.getDrink().getDrinkId());
            stmt.setInt(3, line.getQuantity());
            stmt.setDouble(4, line.getDrink().getPrice());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina totes les línies associades a un tiquet.
     */
    public boolean deleteLinesByTicketId(int ticketId) throws SQLException {
        String sql = "DELETE FROM ticket_lines WHERE ticket_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted >= 0; // 0 si no hi havia línies, també és vàlid
        }
    }
    public double calculateTotalByTicketId(int ticketId) {
        double total = 0.0;
        String sql = "SELECT SUM(quantity * price) AS total FROM ticket_lines WHERE ticket_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public boolean deleteLine(int ticketLineId) {
        String sql = "DELETE FROM ticket_lines WHERE ticket_line_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketLineId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}

