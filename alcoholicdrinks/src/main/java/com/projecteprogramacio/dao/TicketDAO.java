package com.projecteprogramacio.dao;


import com.projecteprogramacio.model.Ticket;
import com.projecteprogramacio.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    // Inserir nou tiquet
    public boolean insertTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (user_id, total, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticket.getUserId());
            stmt.setDouble(2, ticket.getTotal());
            stmt.setString(3, ticket.getStatus());
            stmt.setString(4, ticket.getCreatedAt());
            stmt.setString(5, ticket.getUpdatedAt());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtenir tots els tiquets
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ticket ticket = new Ticket(
                    rs.getInt("ticket_id"),
                    rs.getInt("user_id"),
                    rs.getDouble("total"),
                    rs.getString("status"),
                    rs.getString("created_at"),
                    rs.getString("updated_at")
                );
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    // Obtenir un tiquet per ID
    public Ticket getTicketById(int ticketId) {
        String sql = "SELECT * FROM tickets WHERE ticket_id = ?";
        Ticket ticket = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ticket = new Ticket(
                    rs.getInt("ticket_id"),
                    rs.getInt("user_id"),
                    rs.getDouble("total"),
                    rs.getString("status"),
                    rs.getString("created_at"),
                    rs.getString("updated_at")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ticket;
    }

    // Actualitzar un tiquet
    public boolean updateTicket(Ticket ticket) {
        String sql = "UPDATE tickets SET user_id = ?, total = ?, status = ?, created_at = ?, updated_at = ? WHERE ticket_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticket.getUserId());
            stmt.setDouble(2, ticket.getTotal());
            stmt.setString(3, ticket.getStatus());
            stmt.setString(4, ticket.getCreatedAt());
            stmt.setString(5, ticket.getUpdatedAt());
            stmt.setInt(6, ticket.getTicketId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar un tiquet
    public boolean deleteTicket(int ticketId) {
        String sql = "DELETE FROM tickets WHERE ticket_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticketId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
