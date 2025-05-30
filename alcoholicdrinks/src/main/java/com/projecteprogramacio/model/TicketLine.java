package com.projecteprogramacio.model;

/**
 * Classe que representa una línia d'un tiquet, que conté informació sobre una
 * beguda específica i la quantitat associada dins del tiquet.
 * 
 * Inclou mètodes per obtenir el subtotal (preu * quantitat) i gestionar les
 * propietats.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class TicketLine {

	/** Identificador únic de la línia de tiquet. */
	private int ticketLineId;

	/** Identificador del tiquet al qual pertany la línia. */
	private int ticketId;

	/** Beguda associada a aquesta línia del tiquet. */
	private Drink drink;

	/** Quantitat de la beguda en aquesta línia. */
	private int quantity;

	/**
	 * Constructor complet per crear una línia de tiquet amb tots els atributs.
	 * Normalment usat quan es recupera de la base de dades.
	 * 
	 * @param ticketLineId Identificador de la línia.
	 * @param ticketId     Identificador del tiquet.
	 * @param drink        Beguda associada.
	 * @param quantity     Quantitat de beguda.
	 */
	public TicketLine(int ticketLineId, int ticketId, Drink drink, int quantity) {
		this.ticketLineId = ticketLineId;
		this.ticketId = ticketId;
		this.drink = drink;
		this.quantity = quantity;
	}

	/**
	 * Constructor senzill per crear una línia nova abans d'assignar IDs.
	 * 
	 * @param drink    Beguda associada.
	 * @param quantity Quantitat de beguda.
	 */
	public TicketLine(Drink drink, int quantity) {
		this(0, 0, drink, quantity);
	}

	/** @return Identificador de la línia de tiquet. */
	public int getTicketLineId() {
		return ticketLineId;
	}

	/** @param ticketLineId Nou identificador de la línia de tiquet. */
	public void setTicketLineId(int ticketLineId) {
		this.ticketLineId = ticketLineId;
	}

	/** @return Identificador del tiquet associat. */
	public int getTicketId() {
		return ticketId;
	}

	/** @param ticketId Nou identificador del tiquet associat. */
	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}

	/** @return Beguda associada a la línia. */
	public Drink getDrink() {
		return drink;
	}

	/** @param drink Nova beguda associada a la línia. */
	public void setDrink(Drink drink) {
		this.drink = drink;
	}

	/** @return Quantitat de la beguda. */
	public int getQuantity() {
		return quantity;
	}

	/** @param quantity Nova quantitat de la beguda. */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Calcula el subtotal de la línia, que és el preu de la beguda multiplicat per
	 * la quantitat.
	 * 
	 * @return Subtotal calculat, o 0.0 si la beguda és nul·la.
	 */
	public double getSubtotal() {
		return (drink != null) ? drink.getPrice() * quantity : 0.0;
	}

	/**
	 * Retorna una representació en format de text de la línia, mostrant la
	 * quantitat, el nom de la beguda i el subtotal en euros.
	 * 
	 * @return String descriptiu de la línia de tiquet.
	 */
	@Override
	public String toString() {
		return String.format("%dx %s - %.2f€", quantity, (drink != null ? drink.getName() : "No Drink"), getSubtotal());
	}
}
