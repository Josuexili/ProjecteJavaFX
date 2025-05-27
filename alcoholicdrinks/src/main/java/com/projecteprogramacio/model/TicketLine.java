package com.projecteprogramacio.model;

public class TicketLine {
    private int ticketLineId;
    private int ticketId;
    private Drink drink;
    private int quantity;

    // Constructor complet (per quan l'agafes de la BBDD)
    public TicketLine(int ticketLineId, int ticketId, Drink drink, int quantity) {
        this.ticketLineId = ticketLineId;
        this.ticketId = ticketId;
        this.drink = drink;
        this.quantity = quantity;
    }

    // Constructor senzill (quan estàs creant un nou ticketLine abans de guardar-lo)
    public TicketLine(Drink drink, int quantity) {
        this(0, 0, drink, quantity);
    }

    // Getters i Setters
    public int getTicketLineId() {
        return ticketLineId;
    }

    public void setTicketLineId(int ticketLineId) {
        this.ticketLineId = ticketLineId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Subtotal = preu de la beguda * quantitat
    public double getSubtotal() {
        return (drink != null) ? drink.getPrice() * quantity : 0.0;
    }

    @Override
    public String toString() {
        return String.format("%dx %s - %.2f€", quantity, drink.getName(), getSubtotal());
    }
}

