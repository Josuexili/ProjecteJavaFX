package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.TicketDAO;
import com.projecteprogramacio.model.Ticket;
import com.projecteprogramacio.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.util.List;

public class TicketController {

    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, Integer> colTicketId;
    @FXML private TableColumn<Ticket, Integer> colUserId;
    @FXML private TableColumn<Ticket, Double> colTotal;
    @FXML private TableColumn<Ticket, String> colStatus;
    @FXML private TableColumn<Ticket, String> colCreatedAt;
    @FXML private TableColumn<Ticket, String> colUpdatedAt;

    @FXML private TextField userIdField;
    @FXML private TextField totalField;
    @FXML private TextField statusField;

    @FXML private Label statusLabel;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private ObservableList<Ticket> ticketList;
    private TicketDAO ticketDAO;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            ticketDAO = new TicketDAO(conn);
        } catch (Exception e) {
            statusLabel.setText("Error en la connexió a la BBDD");
            e.printStackTrace();
            return;
        }

        // Configura les columnes de la taula
        colTicketId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTicketId()).asObject());
        colUserId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());
        colTotal.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());
        colStatus.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        colCreatedAt.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreatedAt()));
        colUpdatedAt.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUpdatedAt()));

        loadTickets();

        // Quan es selecciona una fila, carregar la info als camps (excloent createdAt i updatedAt)
        ticketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                userIdField.setText(String.valueOf(newSelection.getUserId()));
                totalField.setText(String.valueOf(newSelection.getTotal()));
                statusField.setText(newSelection.getStatus());
            }
        });
    }

    private void loadTickets() {
        List<Ticket> tickets = ticketDAO.getAllTickets();
        ticketList = FXCollections.observableArrayList(tickets);
        ticketTable.setItems(ticketList);
        statusLabel.setText("Tiquets carregats.");
    }

    @FXML
    private void handleAddTicket() {
        try {
            int userId = Integer.parseInt(userIdField.getText());
            double total = Double.parseDouble(totalField.getText());
            String status = statusField.getText();

            Ticket newTicket = new Ticket(0, userId, total, status, null, null);

            boolean inserted = ticketDAO.insertTicket(newTicket);
            if (inserted) {
                statusLabel.setText("Tiquet afegit correctament.");
                clearFields();
                loadTickets();
            } else {
                statusLabel.setText("Error en afegir tiquet.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Format numèric incorrecte.");
        }
    }

    @FXML
    private void handleUpdateTicket() {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            statusLabel.setText("Selecciona un tiquet per actualitzar.");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdField.getText());
            double total = Double.parseDouble(totalField.getText());
            String status = statusField.getText();

            // No posem createdAt ni updatedAt aquí
            Ticket updatedTicket = new Ticket(selectedTicket.getTicketId(), userId, total, status, null, null);

            boolean updated = ticketDAO.updateTicket(updatedTicket);
            if (updated) {
                statusLabel.setText("Tiquet actualitzat correctament.");
                clearFields();
                loadTickets();
            } else {
                statusLabel.setText("Error en actualitzar tiquet.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Format numèric incorrecte.");
        }
    }

    @FXML
    private void handleDeleteTicket() {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            statusLabel.setText("Selecciona un tiquet per eliminar.");
            return;
        }

        boolean deleted = ticketDAO.deleteTicket(selectedTicket.getTicketId());
        if (deleted) {
            statusLabel.setText("Tiquet eliminat correctament.");
            clearFields();
            loadTickets();
        } else {
            statusLabel.setText("Error en eliminar tiquet.");
        }
    }

    private void clearFields() {
        userIdField.clear();
        totalField.clear();
        statusField.clear();
    }
}

