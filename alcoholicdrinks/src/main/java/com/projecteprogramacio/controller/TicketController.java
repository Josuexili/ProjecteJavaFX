package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.DrinkDAO;
import com.projecteprogramacio.dao.TicketDAO;
import com.projecteprogramacio.dao.TicketLineDAO;
import com.projecteprogramacio.model.Drink;
import com.projecteprogramacio.model.Ticket;
import com.projecteprogramacio.model.TicketLine;
import com.projecteprogramacio.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
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
    @FXML private Button addButton, updateButton, deleteButton;
    @FXML private Label statusLabel;
    @FXML private Button goToCreateTicketButton;

    private ObservableList<Ticket> ticketList;
    private TicketDAO ticketDAO;

    // Línies de tiquet
    @FXML private TableView<TicketLine> ticketLinesTable;
    @FXML private TableColumn<TicketLine, Integer> colLineId;
    @FXML private TableColumn<TicketLine, Integer> colProductId;
    @FXML private TableColumn<TicketLine, Integer> colQuantity;
    @FXML private TableColumn<TicketLine, Double> colPrice;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;
    @FXML private ComboBox<Drink> drinkComboBox;
    @FXML private Button addLineButton, deleteLineButton;

    private ObservableList<TicketLine> ticketLineList;
    private TicketLineDAO ticketLineDAO;
    private DrinkDAO drinkDAO;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            ticketDAO = new TicketDAO(conn);
            ticketLineDAO = new TicketLineDAO(conn);
            drinkDAO = new DrinkDAO(conn);
        } catch (Exception e) {
            statusLabel.setText("Error en la connexió a la base de dades.");
            e.printStackTrace();
            disableAllButtons();
            return;
        }

        // Columnes taula tiquet
        colTicketId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTicketId()).asObject());
        colUserId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());
        colTotal.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        colCreatedAt.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreatedAt()));
        colUpdatedAt.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUpdatedAt()));

        // Columnes línies de tiquet
        colLineId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTicketLineId()).asObject());
        colProductId.setCellValueFactory(cellData -> {
            Drink drink = cellData.getValue().getDrink();
            int drinkId = (drink != null) ? drink.getDrinkId() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(drinkId).asObject();
        });
        colQuantity.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(cellData -> {
            Drink drink = cellData.getValue().getDrink();
            double price = (drink != null) ? drink.getPrice() : 0.0;
            return new javafx.beans.property.SimpleDoubleProperty(price).asObject();
        });

        loadTickets();

        ticketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
                loadTicketLines(newSelection.getTicketId());
            } else {
                clearFields();
                clearTicketLines();
            }
        });

        List<Drink> drinks = drinkDAO.getAllDrinks();
        drinkComboBox.setItems(FXCollections.observableArrayList(drinks));
        drinkComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Drink drink, boolean empty) {
                super.updateItem(drink, empty);
                setText((drink == null || empty) ? "" : drink.getName());
            }
        });
        drinkComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Drink drink, boolean empty) {
                super.updateItem(drink, empty);
                setText((drink == null || empty) ? "" : drink.getName());
            }
        });

        clearFields();
        clearTicketLines();
        disableLineButtons(true);
    }

    private void disableAllButtons() {
        addButton.setDisable(true);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        disableLineButtons(true);
    }

    private void disableLineButtons(boolean disable) {
        addLineButton.setDisable(disable);
        deleteLineButton.setDisable(disable);
    }

    private void populateFields(Ticket ticket) {
        userIdField.setText(String.valueOf(ticket.getUserId()));
        totalField.setText(String.format("%.2f", ticket.getTotal()));
        statusField.setText(ticket.getStatus());
        disableLineButtons(false);
    }

    private void clearFields() {
        userIdField.clear();
        totalField.clear();
        statusField.clear();
        disableLineButtons(true);
    }

    private void loadTickets() {
        List<Ticket> tickets = ticketDAO.getAllTickets();
        ticketList = FXCollections.observableArrayList(tickets);
        ticketTable.setItems(ticketList);
        statusLabel.setText("Tiquets carregats.");
    }

    private void loadTicketLines(int ticketId) {
        List<TicketLine> lines = ticketLineDAO.getLinesByTicketId(ticketId);
        ticketLineList = FXCollections.observableArrayList(lines);
        ticketLinesTable.setItems(ticketLineList);
    }

    private void clearTicketLines() {
        if (ticketLineList != null) ticketLineList.clear();
        ticketLinesTable.setItems(null);
    }

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
                clearTicketLines();
                loadTickets();
            } else {
                statusLabel.setText("Error en eliminar tiquet.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperat en eliminar tiquet.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddLine() {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            statusLabel.setText("Selecciona un tiquet per afegir línies.");
            return;
        }

        try {
            Drink drink = drinkComboBox.getValue();
            if (drink == null) {
                statusLabel.setText("Has de seleccionar una beguda.");
                return;
            }

            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                statusLabel.setText("La quantitat ha de ser major que zero.");
                return;
            }

            TicketLine newLine = new TicketLine(0, selectedTicket.getTicketId(), drink, quantity);
            if (ticketLineDAO.insertLine(newLine)) {
                statusLabel.setText("Línia afegida correctament.");
                loadTicketLines(selectedTicket.getTicketId());
                updateTicketTotal(selectedTicket.getTicketId());
                clearLineFields();
            } else {
                statusLabel.setText("Error en afegir línia.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Format numèric incorrecte (Quantitat).");
        } catch (Exception e) {
            statusLabel.setText("Error inesperat en afegir línia.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteLine() {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        TicketLine selectedLine = ticketLinesTable.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            statusLabel.setText("Selecciona un tiquet.");
            return;
        }
        if (selectedLine == null) {
            statusLabel.setText("Selecciona una línia per eliminar.");
            return;
        }

        try {
            if (ticketLineDAO.deleteLine(selectedLine.getTicketLineId())) {
                statusLabel.setText("Línia eliminada correctament.");
                loadTicketLines(selectedTicket.getTicketId());
                updateTicketTotal(selectedTicket.getTicketId());
            } else {
                statusLabel.setText("Error en eliminar línia.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperat en eliminar línia.");
            e.printStackTrace();
        }
    }

    private void clearLineFields() {
        drinkComboBox.getSelectionModel().clearSelection();
        quantityField.clear();
        priceField.clear();
    }

    private void updateTicketTotal(int ticketId) {
        try {
            List<TicketLine> lines = ticketLineDAO.getLinesByTicketId(ticketId);
            double newTotal = 0.0;
            for (TicketLine line : lines) {
                Drink drink = line.getDrink();
                if (drink != null) {
                    newTotal += line.getQuantity() * drink.getPrice();
                }
            }

            Ticket ticket = ticketDAO.getTicketById(ticketId);
            if (ticket != null) {
                ticket.setTotal(newTotal);
                ticketDAO.updateTicket(ticket);
                loadTickets();
                ticketTable.getSelectionModel().select(ticket);
                populateFields(ticket);
            }
        } catch (Exception e) {
            statusLabel.setText("Error en actualitzar el total del tiquet.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToCreateTicket() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/projecteprogramacio/view/createTicket.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) goToCreateTicketButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear nou tiquet");
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error en carregar la pantalla de crear tiquet.");
            e.printStackTrace();
        }
    }
}

