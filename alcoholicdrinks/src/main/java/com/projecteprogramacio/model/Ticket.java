package com.projecteprogramacio.model;

import java.util.ArrayList;
import java.util.List;

public class Ticket {
    private int ticketId;
    private int userId;
    private double total;
    private String status;
    private String createdAt;
    private String updatedAt;

    private List<TicketLine> lines = new ArrayList<>();

    // Constructors
    public Ticket() {}

    public Ticket(int ticketId, int userId, double total, String status, String createdAt, String updatedAt) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.total = total;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters i Setters
    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Gestionar línies de tiquet
    public List<TicketLine> getLines() {
        return lines;
    }

    public void setLines(List<TicketLine> lines) {
        this.lines = lines;
        recalculateTotal(); // recalcula el total si es canvien totes les línies
    }

    public void addLine(TicketLine line) {
        line.setTicketId(this.ticketId); // assigna el ticketId
        lines.add(line);
        recalculateTotal();
    }

    public void removeLine(TicketLine line) {
        lines.remove(line);
        recalculateTotal();
    }

    public void clearLines() {
        lines.clear();
        recalculateTotal();
    }

    // Recalcular el total
    public void recalculateTotal() {
        total = lines.stream()
                .mapToDouble(TicketLine::getSubtotal)
                .sum();
    }

    @Override
    public String toString() {
        return "Ticket #" + ticketId + " - " + status + " - Total: " + String.format("%.2f€", total);
    }
}

