package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.Ticket;
import com.projecteprogramacio.model.TicketLine;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Connection conn;
    private final TicketLineDAO ticketLineDAO;

    public TicketDAO(Connection conn) {
        this.conn = conn;
        this.ticketLineDAO = new TicketLineDAO(conn);
    }

    public boolean insertTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (user_id, total, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        String now = LocalDateTime.now().format(formatter);

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, ticket.getUserId());
                stmt.setDouble(2, ticket.getTotal());
                stmt.setString(3, ticket.getStatus());
                stmt.setString(4, now);
                stmt.setString(5, now);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Obtenir l'ID generat per el ticket inserit
            try (Statement idStmt = conn.createStatement();
                 ResultSet generatedKeys = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    ticket.setTicketId(generatedId);

                    // Inserir línies del tiquet
                    for (TicketLine line : ticket.getLines()) {
                        line.setTicketId(generatedId);
                        if (!ticketLineDAO.insertLine(line)) {
                            conn.rollback();
                            return false;
                        }
                    }
                } else {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets ORDER BY created_at DESC";

        try (Statement stmt = conn.createStatement();
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
                // Carregar línies associades
                ticket.setLines(ticketLineDAO.getLinesByTicketId(ticket.getTicketId()));
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public Ticket getTicketById(int ticketId) {
        String sql = "SELECT * FROM tickets WHERE ticket_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Ticket ticket = new Ticket(
                            rs.getInt("ticket_id"),
                            rs.getInt("user_id"),
                            rs.getDouble("total"),
                            rs.getString("status"),
                            rs.getString("created_at"),
                            rs.getString("updated_at")
                    );
                    ticket.setLines(ticketLineDAO.getLinesByTicketId(ticketId));
                    return ticket;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateTicket(Ticket ticket) {
        String sql = "UPDATE tickets SET user_id = ?, total = ?, status = ?, updated_at = ? WHERE ticket_id = ?";
        String now = LocalDateTime.now().format(formatter);

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, ticket.getUserId());
                stmt.setDouble(2, ticket.getTotal());
                stmt.setString(3, ticket.getStatus());
                stmt.setString(4, now);
                stmt.setInt(5, ticket.getTicketId());

                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Eliminar línies antigues del ticket
            if (!ticketLineDAO.deleteLinesByTicketId(ticket.getTicketId())) {
                conn.rollback();
                return false;
            }

            // Inserir línies noves
            for (TicketLine line : ticket.getLines()) {
                line.setTicketId(ticket.getTicketId());
                if (!ticketLineDAO.insertLine(line)) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean deleteTicket(int ticketId) {
        try {
            conn.setAutoCommit(false);

            // Esborra les línies relacionades primer
            if (!ticketLineDAO.deleteLinesByTicketId(ticketId)) {
                conn.rollback();
                return false;
            }

            String sql = "DELETE FROM tickets WHERE ticket_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, ticketId);
                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<Ticket> getTicketsByStatus(String status) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE status = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ticket ticket = new Ticket(
                            rs.getInt("ticket_id"),
                            rs.getInt("user_id"),
                            rs.getDouble("total"),
                            rs.getString("status"),
                            rs.getString("created_at"),
                            rs.getString("updated_at")
                    );
                    ticket.setLines(ticketLineDAO.getLinesByTicketId(ticket.getTicketId()));
                    tickets.add(ticket);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public void updateTicketStatus(int ticketId, String newStatus) {
        String sql = "UPDATE tickets SET status = ?, updated_at = ? WHERE ticket_id = ?";
        String now = LocalDateTime.now().format(formatter);

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, now);
            stmt.setInt(3, ticketId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                System.err.println("No s'ha trobat cap tiquet amb id: " + ticketId);
            }
        } catch (SQLException e) {
            System.err.println("Error actualitzant estat del tiquet: " + e.getMessage());
        }
    }
}

