package com.projecteprogramacio.dao;

import com.projecteprogramacio.model.Ticket;
import com.projecteprogramacio.model.TicketLine;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO per gestionar l'accés a la taula tickets i realitzar operacions
 * CRUD relacionades.
 * <p>
 * També gestiona les línies de tiquet mitjançant TicketLineDAO i s’encarrega de
 * la gestió de transaccions.
 * </p>
 * 
 * @author Josuè González
 * @version 1.0
 */
public class TicketDAO {

	/** Formatador de data i hora per SQLite */
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/** Connexió JDBC activa */
	private final Connection conn;

	/** DAO per a les línies del tiquet */
	private final TicketLineDAO ticketLineDAO;

	/**
	 * Constructor que inicialitza el DAO amb la connexió a la base de dades.
	 * 
	 * @param conn connexió JDBC activa
	 */
	public TicketDAO(Connection conn) {
		this.conn = conn;
		this.ticketLineDAO = new TicketLineDAO(conn);
	}

	/**
	 * Insereix un nou tiquet amb les seves línies a la base de dades. La inserció
	 * és atòmica (transacció).
	 * 
	 * @param ticket tiquet a inserir
	 * @return {@code true} si la inserció ha estat correcta; {@code false} en cas
	 *         contrari
	 */
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

			// Obtenir l'ID generat pel ticket inserit
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

	/**
	 * Obté tots els tiquets ordenats per data de creació descendent.
	 * 
	 * @return llista de tiquets
	 */
	public List<Ticket> getAllTickets() {
		List<Ticket> tickets = new ArrayList<>();
		String sql = "SELECT * FROM tickets ORDER BY created_at DESC";

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Ticket ticket = new Ticket(rs.getInt("ticket_id"), rs.getInt("user_id"), rs.getDouble("total"),
						rs.getString("status"), rs.getString("created_at"), rs.getString("updated_at"));
				ticket.setLines(ticketLineDAO.getLinesByTicketId(ticket.getTicketId()));
				tickets.add(ticket);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tickets;
	}

	/**
	 * Obté un tiquet pel seu identificador.
	 * 
	 * @param ticketId identificador del tiquet
	 * @return el tiquet corresponent o {@code null} si no existeix
	 */
	public Ticket getTicketById(int ticketId) {
		String sql = "SELECT * FROM tickets WHERE ticket_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, ticketId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Ticket ticket = new Ticket(rs.getInt("ticket_id"), rs.getInt("user_id"), rs.getDouble("total"),
							rs.getString("status"), rs.getString("created_at"), rs.getString("updated_at"));
					ticket.setLines(ticketLineDAO.getLinesByTicketId(ticketId));
					return ticket;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Actualitza un tiquet i les seves línies associades. La operació és atòmica.
	 * 
	 * @param ticket tiquet amb les dades actualitzades
	 * @return {@code true} si l'actualització ha tingut èxit; {@code false} en cas
	 *         contrari
	 */
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

			if (!ticketLineDAO.deleteLinesByTicketId(ticket.getTicketId())) {
				conn.rollback();
				return false;
			}

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

	/**
	 * Elimina un tiquet i les seves línies associades. La eliminació és atòmica.
	 * 
	 * @param ticketId identificador del tiquet a eliminar
	 * @return {@code true} si la eliminació ha tingut èxit; {@code false} en cas
	 *         contrari
	 */
	public boolean deleteTicket(int ticketId) {
		try {
			conn.setAutoCommit(false);

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

	/**
	 * Obté els tiquets que tenen un estat concret.
	 * 
	 * @param status estat dels tiquets a filtrar
	 * @return llista de tiquets amb l'estat especificat
	 */
	public List<Ticket> getTicketsByStatus(String status) {
		List<Ticket> tickets = new ArrayList<>();
		String sql = "SELECT * FROM tickets WHERE status = ? ORDER BY created_at DESC";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, status);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Ticket ticket = new Ticket(rs.getInt("ticket_id"), rs.getInt("user_id"), rs.getDouble("total"),
							rs.getString("status"), rs.getString("created_at"), rs.getString("updated_at"));
					ticket.setLines(ticketLineDAO.getLinesByTicketId(ticket.getTicketId()));
					tickets.add(ticket);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tickets;
	}

	/**
	 * Actualitza l’estat d’un tiquet.
	 * 
	 * @param ticketId  identificador del tiquet
	 * @param newStatus nou estat per al tiquet
	 */
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
