package com.projecteprogramacio.model;


public class TicketItem {
    private int itemId;
    private int ticketId;
    private int drinkId;
    private int quantity;
    private double price;

    public TicketItem() {}

    public TicketItem(int itemId, int ticketId, int drinkId, int quantity, double price) {
        this.itemId = itemId;
        this.ticketId = ticketId;
        this.drinkId = drinkId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(int drinkId) {
        this.drinkId = drinkId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
