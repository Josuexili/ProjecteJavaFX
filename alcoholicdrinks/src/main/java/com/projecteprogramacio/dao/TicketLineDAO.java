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
     * Obté totes les línies d’un tiquet a partir del seu ID.
     * Recupera les línies i el detall de la beguda associada a cada línia.
     * 
     * Aquest mètode és complementari al mètode getTicketById() del TicketDAO, 
     * que utilitza aquest per carregar les línies associades a un ticket.
     *
     * @param ticketId ID del tiquet del qual volem obtenir les línies.
     * @return Llista de línies de tiquet associades.
     */
    public List<TicketLine> getLinesByTicketId(int ticketId) {
        List<TicketLine> lines = new ArrayList<>();
        String sql = "SELECT * FROM ticket_lines WHERE ticket_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int drinkId = rs.getInt("drink_id");
                    // Obtenció detallada de la beguda associada per a cada línia
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
     * Insereix una nova línia dins d’un tiquet a la base de dades.
     * 
     * Aquest mètode s’utilitza dins del mètode insertTicket() i updateTicket() del TicketDAO
     * per afegir les línies associades després d’inserir o actualitzar el tiquet.
     *
     * @param line Objecte TicketLine que conté la informació a inserir.
     * @return true si la inserció s’ha realitzat correctament, false en cas contrari.
     * @throws SQLException Si hi ha un error durant la inserció.
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
     * Elimina totes les línies associades a un tiquet específic.
     * 
     * Aquest mètode es fa servir abans d’actualitzar les línies d’un tiquet amb updateTicket() del TicketDAO,
     * per assegurar que les línies antigues s’esborren abans d’inserir les noves.
     *
     * @param ticketId ID del tiquet del qual es volen eliminar les línies.
     * @return true si l’operació s’ha realitzat correctament, inclòs si no hi havia línies a eliminar.
     * @throws SQLException Si hi ha un error durant l’eliminació.
     */
    public boolean deleteLinesByTicketId(int ticketId) throws SQLException {
        String sql = "DELETE FROM ticket_lines WHERE ticket_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted >= 0; // 0 si no hi havia línies, també és vàlid
        }
    }

    /**
     * Calcula el total del tiquet sumant la quantitat per el preu de cada línia.
     * 
     * Pot ser útil per validar o recalcular el total d’un tiquet després d’afegir o eliminar línies.
     *
     * @param ticketId ID del tiquet del qual volem calcular el total.
     * @return Suma total de totes les línies del tiquet.
     */
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

    /**
     * Elimina una línia de tiquet concreta donat el seu ID.
     * 
     * Pot ser útil per eliminar línies específiques sense eliminar tot el tiquet.
     *
     * @param ticketLineId ID de la línia que es vol eliminar.
     * @return true si la línia s’ha eliminat correctament, false en cas contrari.
     */
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

