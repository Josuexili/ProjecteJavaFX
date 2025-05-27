package com.projecteprogramacio.model;

import com.projecteprogramacio.dao.DrinkDAO;
import com.projecteprogramacio.model.*;

import java.util.ArrayList;
import java.util.List;

public class TicketMapper {

    private DrinkDAO drinkDAO; // Per carregar el Drink complet des de la BD

    public TicketMapper(DrinkDAO drinkDAO) {
        this.drinkDAO = drinkDAO;
    }

    // Convertir de TicketItem (BD) a TicketLine (app)
    public TicketLine toTicketLine(TicketItem item) {
        Drink drink = drinkDAO.getDrinkById(item.getDrinkId());
        return new TicketLine(item.getItemId(), item.getTicketId(), drink, item.getQuantity());
    }

    // Convertir de TicketLine (app) a TicketItem (BD)
    public TicketItem toTicketItem(TicketLine line) {
        int drinkId = (line.getDrink() != null) ? line.getDrink().getDrinkId() : 0;
        double price = (line.getDrink() != null) ? line.getDrink().getPrice() : 0.0;
        return new TicketItem(line.getTicketLineId(), line.getTicketId(), drinkId, line.getQuantity(), price);
    }

    // Convertir llista de TicketItem a llista de TicketLine
    public List<TicketLine> toTicketLineList(List<TicketItem> items) {
        List<TicketLine> lines = new ArrayList<>();
        for (TicketItem item : items) {
            lines.add(toTicketLine(item));
        }
        return lines;
    }

    // Convertir llista de TicketLine a llista de TicketItem
    public List<TicketItem> toTicketItemList(List<TicketLine> lines) {
        List<TicketItem> items = new ArrayList<>();
        for (TicketLine line : lines) {
            items.add(toTicketItem(line));
        }
        return items;
    }
}
