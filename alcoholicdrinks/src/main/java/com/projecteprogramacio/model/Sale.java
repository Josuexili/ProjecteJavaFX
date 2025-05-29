package com.projecteprogramacio.model;

/**
 * Classe que representa una venda realitzada.
 * 
 * Conté la informació bàsica d'una venda, com l'identificador, el tiquet associat,
 * l'import total, la data de creació i el total acumulat.
 * 
 * Aquesta classe s'utilitza com a model per a la taula `sales` a la base de dades.
 * 
 * Inclou constructors per a crear instàncies amb o sense dades inicials, i getters/setters
 * per accedir i modificar les propietats.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class Sale {

    /** Identificador únic de la venda. */
    private int saleId;

    /** Identificador del tiquet associat a la venda. */
    private int ticketId;

    /** Import total de la venda. */
    private double totalAmount;

    /** Data i hora de creació de la venda, en format String. */
    private String createdAt;

    /** Total acumulat fins a aquesta venda. */
    private double cumulativeTotal;

    /**
     * Constructor buit per defecte.
     * Necessari per a frameworks o ús flexible.
     */
    public Sale() {}

    /**
     * Constructor complet que inicialitza tots els camps.
     * 
     * @param saleId          Identificador de la venda.
     * @param ticketId        Identificador del tiquet associat.
     * @param totalAmount     Import total de la venda.
     * @param createdAt       Data de creació de la venda.
     * @param cumulativeTotal Total acumulat fins a la venda.
     */
    public Sale(int saleId, int ticketId, double totalAmount, String createdAt, double cumulativeTotal) {
        this.saleId = saleId;
        this.ticketId = ticketId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.cumulativeTotal = cumulativeTotal;
    }

    /** @return Identificador únic de la venda. */
    public int getSaleId() {
        return saleId;
    }

    /** @param saleId Nou identificador de la venda. */
    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    /** @return Identificador del tiquet associat. */
    public int getTicketId() {
        return ticketId;
    }

    /** @param ticketId Nou identificador del tiquet associat. */
    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    /** @return Import total de la venda. */
    public double getTotalAmount() {
        return totalAmount;
    }

    /** @param totalAmount Nou import total de la venda. */
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /** @return Data i hora de creació de la venda. */
    public String getCreatedAt() {
        return createdAt;
    }

    /** @param createdAt Nova data i hora de creació. */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /** @return Total acumulat fins a aquesta venda. */
    public double getCumulativeTotal() {
        return cumulativeTotal;
    }

    /** @param cumulativeTotal Nou total acumulat fins a la venda. */
    public void setCumulativeTotal(double cumulativeTotal) {
        this.cumulativeTotal = cumulativeTotal;
    }
}

