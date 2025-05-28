package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.TicketDAO;
import com.projecteprogramacio.model.Ticket;
import com.projecteprogramacio.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class TicketController {

    // Constants per estats
    private static final String STATUS_CLOSED = "closed";
    private static final String STATUS_CREATED = "created";

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
            statusLabel.setText("Error en la connexió a la base de dades.");
            e.printStackTrace();
            disableAllButtons();
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
                populateFields(newSelection);
            } else {
                clearFields();
            }
        });
    }

    private void disableAllButtons() {
        addButton.setDisable(true);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void populateFields(Ticket ticket) {
        userIdField.setText(String.valueOf(ticket.getUserId()));
        totalField.setText(String.valueOf(ticket.getTotal()));
        statusField.setText(ticket.getStatus());
    }

    private void clearFields() {
        userIdField.clear();
        totalField.clear();
        statusField.clear();
    }

    private void loadTickets() {
        List<Ticket> tickets = ticketDAO.getAllTickets();
        ticketList = FXCollections.observableArrayList(tickets);
        ticketTable.setItems(ticketList);
        statusLabel.setText("Tiquets carregats.");
    }

    // ------------ Accions UI ------------

    @FXML
    private void handleAddTicket() {
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            double total = Double.parseDouble(totalField.getText().trim());
            String status = statusField.getText().trim();

            if (status.isEmpty()) {
                statusLabel.setText("El camp estat no pot estar buit.");
                return;
            }

            Ticket newTicket = new Ticket(0, userId, total, status, null, null);

            if (ticketDAO.insertTicket(newTicket)) {
                statusLabel.setText("Tiquet afegit correctament.");
                clearFields();
                loadTickets();
            } else {
                statusLabel.setText("Error en afegir tiquet.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Format numèric incorrecte (UserId o Total).");
        } catch (Exception e) {
            statusLabel.setText("Error inesperat en afegir tiquet.");
            e.printStackTrace();
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
            int userId = Integer.parseInt(userIdField.getText().trim());
            double total = Double.parseDouble(totalField.getText().trim());
            String status = statusField.getText().trim();

            if (status.isEmpty()) {
                statusLabel.setText("El camp estat no pot estar buit.");
                return;
            }

            // Actualitzem només camps que l'usuari pot modificar
            selectedTicket.setUserId(userId);
            selectedTicket.setTotal(total);
            selectedTicket.setStatus(status);

            if (ticketDAO.updateTicket(selectedTicket)) {
                statusLabel.setText("Tiquet actualitzat correctament.");
                clearFields();
                loadTickets();
            } else {
                statusLabel.setText("Error en actualitzar tiquet.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Format numèric incorrecte (UserId o Total).");
        } catch (Exception e) {
            statusLabel.setText("Error inesperat en actualitzar tiquet.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteTicket() {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            statusLabel.setText("Selecciona un tiquet per eliminar.");
            return;
        }

        try {
            if (ticketDAO.deleteTicket(selectedTicket.getTicketId())) {
                statusLabel.setText("Tiquet eliminat correctament.");
                clearFields();
                loadTickets();
            } else {
                statusLabel.setText("Error en eliminar tiquet.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperat en eliminar tiquet.");
            e.printStackTrace();
        }
    }

   
    

}

