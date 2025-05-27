package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.Ticket;
import com.projecteprogramacio.model.TicketLine;
import com.projecteprogramacio.util.Database;

import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Connection conn;
    private final TicketLineDAO ticketLineDAO;

    // Constructor: rep connexió i crea TicketLineDAO amb la mateixa connexió
    public TicketDAO(Connection conn) {
        this.conn = conn;
        this.ticketLineDAO = new TicketLineDAO(conn);
    }

    // Inserir nou tiquet amb línies dins una transacció
    public boolean insertTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (user_id, total, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        String now = LocalDateTime.now().format(formatter);

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        ticket.setTicketId(generatedId);

                        // Inserir línies
                        for (TicketLine line : ticket.getLines()) {
                            line.setTicketId(generatedId);
                            boolean inserted = ticketLineDAO.insertLine(line);
                            if (!inserted) {
                                conn.rollback();
                                return false;
                            }
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
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

    // Obtenir tots els tiquets amb línies
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets";

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

                List<TicketLine> lines = ticketLineDAO.getLinesByTicketId(ticket.getTicketId());
                ticket.setLines(lines);

                tickets.add(ticket);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    // Obtenir tiquet per ID amb línies
    public Ticket getTicketById(int ticketId) {
        String sql = "SELECT * FROM tickets WHERE ticket_id = ?";
        Ticket ticket = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ticket = new Ticket(
                            rs.getInt("ticket_id"),
                            rs.getInt("user_id"),
                            rs.getDouble("total"),
                            rs.getString("status"),
                            rs.getString("created_at"),
                            rs.getString("updated_at")
                    );

                    List<TicketLine> lines = ticketLineDAO.getLinesByTicketId(ticketId);
                    ticket.setLines(lines);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ticket;
    }

    // Actualitzar tiquet i línies dins una transacció
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

                // Esborrar línies antigues i inserir noves
                boolean deletedLines = ticketLineDAO.deleteLinesByTicketId(ticket.getTicketId());
                if (!deletedLines) {
                    conn.rollback();
                    return false;
                }

                for (TicketLine line : ticket.getLines()) {
                    line.setTicketId(ticket.getTicketId());
                    boolean insertedLine = ticketLineDAO.insertLine(line);
                    if (!insertedLine) {
                        conn.rollback();
                        return false;
                    }
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

    // Esborrar tiquet i línies en transacció
    public boolean deleteTicket(int ticketId) {
        try {
            conn.setAutoCommit(false);

            boolean deletedLines = ticketLineDAO.deleteLinesByTicketId(ticketId);
            if (!deletedLines) {
                conn.rollback();
                return false;
            }

            String sql = "DELETE FROM tickets WHERE ticket_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, ticketId);
                int deletedTicket = stmt.executeUpdate();

                if (deletedTicket == 0) {
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

    // Exemple d’ús amb ObservableList, és estàtic però podria eliminar-se o adaptar-se per fer servir la connexió externa
    public static boolean saveTicketWithLines(Ticket ticket, ObservableList<TicketLine> ticketLines) {
        String insertTicketSQL = "INSERT INTO tickets (user_id, total, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        String insertLineSQL = "INSERT INTO ticket_lines (ticket_id, drink_id, quantity, price) VALUES (?, ?, ?, ?)";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now().format(formatter);

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ticketStmt = conn.prepareStatement(insertTicketSQL, Statement.RETURN_GENERATED_KEYS)) {
                ticketStmt.setInt(1, ticket.getUserId());
                ticketStmt.setDouble(2, ticket.getTotal());
                ticketStmt.setString(3, ticket.getStatus());
                ticketStmt.setString(4, now);
                ticketStmt.setString(5, now);

                int rowsInserted = ticketStmt.executeUpdate();
                if (rowsInserted == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = ticketStmt.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        conn.rollback();
                        return false;
                    }
                    int ticketId = generatedKeys.getInt(1);
                    ticket.setTicketId(ticketId);

                    try (PreparedStatement lineStmt = conn.prepareStatement(insertLineSQL)) {
                        for (TicketLine line : ticketLines) {
                            lineStmt.setInt(1, ticketId);
                            lineStmt.setInt(2, line.getDrink().getDrinkId());
                            lineStmt.setInt(3, line.getQuantity());
                            lineStmt.setDouble(4, line.getSubtotal() / line.getQuantity());
                            lineStmt.addBatch();
                        }
                        lineStmt.executeBatch();
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}


