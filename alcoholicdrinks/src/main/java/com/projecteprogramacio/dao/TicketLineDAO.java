package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.Drink;
import com.projecteprogramacio.model.TicketLine;
import com.projecteprogramacio.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketLineDAO {

    private final DrinkDAO drinkDAO = new DrinkDAO();
    private final Connection conn;  // <-- variable per guardar la connexió

    // Constructor que rep la connexió i l'assigna
    public TicketLineDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Recupera totes les línies de tiquet associades a un ticketId.
     * 
     * @param ticketId Id del tiquet
     * @return Llista de TicketLine
     */
    public List<TicketLine> getLinesByTicketId(int ticketId) {
        List<TicketLine> lines = new ArrayList<>();
        String sql = "SELECT * FROM ticket_lines WHERE ticket_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {  // Utilitza la connexió guardada
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int drinkId = rs.getInt("drink_id");
                    Drink drink = drinkDAO.getDrinkById(drinkId);

                    TicketLine line = new TicketLine(
                            rs.getInt("ticket_line_id"),
                            rs.getInt("ticket_id"),
                            drink,
                            rs.getInt("quantity")
                    );
                    lines.add(line);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * Insereix una línia de tiquet utilitzant una connexió passada (per gestionar transaccions).
     * 
     * @param line Objecte TicketLine a inserir
     * @param conn Connexió activa a la base de dades
     * @return true si la inserció és correcta, false en cas contrari
     * @throws SQLException Lança excepció en cas d'error SQL
     */
    public boolean insertLine(TicketLine line, Connection conn) throws SQLException {
        String sql = "INSERT INTO ticket_lines (ticket_id, drink_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, line.getTicketId());
            stmt.setInt(2, line.getDrink().getDrinkId());
            stmt.setInt(3, line.getQuantity());
            stmt.setDouble(4, line.getDrink().getPrice());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Insereix una línia de tiquet sense passar connexió (sense suport per transaccions).
     * 
     * @param line Objecte TicketLine a inserir
     * @return true si la inserció és correcta, false en cas contrari
     */
    public boolean insertLine(TicketLine line) {
        String sql = "INSERT INTO ticket_lines (ticket_id, drink_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, line.getTicketId());
            stmt.setInt(2, line.getDrink().getDrinkId());
            stmt.setInt(3, line.getQuantity());
            stmt.setDouble(4, line.getDrink().getPrice());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Esborra totes les línies d'un tiquet usant una connexió passada (per transacció).
     * 
     * @param ticketId Id del tiquet
     * @param conn Connexió activa
     * @return true si s'han esborrat les línies correctament
     * @throws SQLException Lança excepció en cas d'error SQL
     */
    public boolean deleteLinesByTicketId(int ticketId, Connection conn) throws SQLException {
        String sql = "DELETE FROM ticket_lines WHERE ticket_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            stmt.executeUpdate();
            return true;
        }
    }

    /**
     * Esborra totes les línies d'un tiquet sense connexió passada (sense transaccions).
     * 
     * @param ticketId Id del tiquet
     * @return true si s'han esborrat les línies correctament
     */
    public boolean deleteLinesByTicketId(int ticketId) {
        String sql = "DELETE FROM ticket_lines WHERE ticket_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticketId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
