package com.projecteprogramacio.model;



public class Sale {
    private int saleId;
    private int ticketId;
    private double totalAmount;
    private String createdAt;
    private double cumulativeTotal;

    // Constructor buit
    public Sale() {}

    // Constructor complet
    public Sale(int saleId, int ticketId, double totalAmount, String createdAt, double cumulativeTotal) {
        this.saleId = saleId;
        this.ticketId = ticketId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.cumulativeTotal = cumulativeTotal;
    }

    // Getters i setters
    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getCumulativeTotal() {
        return cumulativeTotal;
    }

    public void setCumulativeTotal(double cumulativeTotal) {
        this.cumulativeTotal = cumulativeTotal;
    }
}
