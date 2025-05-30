package com.projecteprogramacio.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import com.projecteprogramacio.dao.DrinkDAO;
import com.projecteprogramacio.dao.TicketDAO;
import com.projecteprogramacio.dao.TicketLineDAO;
import com.projecteprogramacio.model.Drink;
import com.projecteprogramacio.model.Ticket;
import com.projecteprogramacio.model.TicketLine;
import com.projecteprogramacio.model.User;
import com.projecteprogramacio.util.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Controlador per gestionar la visualització, creació, actualització i eliminació
 * de tiquets i línies de tiquet a l'aplicació.
 * <p>
 * Gestiona la interacció amb la base de dades a través dels DAO i actualitza
 * la vista JavaFX corresponent.
 * </p>
 * 
 * @author 
 */
public class TicketController {

    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, Integer> colTicketId, colUserId;
    @FXML private TableColumn<Ticket, Double> colTotal;
    @FXML private TableColumn<Ticket, String> colStatus, colCreatedAt, colUpdatedAt;

    @FXML private TextField userIdField, totalField, statusField;
    @FXML private Button addButton, updateButton, deleteButton;
    @FXML private Label statusLabel;
    @FXML private Button goToCreateTicketButton;

    @FXML private TableView<TicketLine> ticketLinesTable;
    @FXML private TableColumn<TicketLine, Integer> colLineId, colProductId, colQuantity;
    @FXML private TableColumn<TicketLine, Double> colPrice;

    @FXML private TextField quantityField, priceField;
    @FXML private ComboBox<Drink> drinkComboBox;
    @FXML private Button addLineButton, deleteLineButton;

    private ObservableList<Ticket> ticketList;
    private ObservableList<TicketLine> ticketLineList;
    private TicketDAO ticketDAO;
    private TicketLineDAO ticketLineDAO;
    private DrinkDAO drinkDAO;
    
    private User loggedUser; // Usuari loguejat

    /**
     * Estableix l'usuari que ha fet login.
     * 
     * @param user Usuari loguejat
     */
    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    /**
     * Inicialitza el controlador. Estableix la connexió amb la base de dades,
     * configura les taules i combobox, i carrega les dades inicials.
     */
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
        
        setupTicketTable();
        setupLineTable();
        setupDrinkComboBox();

        loadTickets();
        clearFields();
        clearTicketLines();
        disableLineButtons(true);

        ticketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateFields(newSel);
                loadTicketLines(newSel.getTicketId());
            } else {
                clearFields();
                clearTicketLines();
            }
        });
    }

    /**
     * Configura les columnes de la taula de tiquets amb els valors corresponents.
     */
    private void setupTicketTable() {
        colTicketId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getTicketId()).asObject());
        colUserId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getUserId()).asObject());
        colTotal.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getTotal()).asObject());
        colStatus.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        colCreatedAt.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCreatedAt()));
        colUpdatedAt.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUpdatedAt()));
    }

    /**
     * Configura les columnes de la taula de línies de tiquet.
     */
    private void setupLineTable() {
        colLineId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getTicketLineId()).asObject());
        colProductId.setCellValueFactory(cell -> {
            Drink drink = cell.getValue().getDrink();
            return new javafx.beans.property.SimpleIntegerProperty(drink != null ? drink.getDrinkId() : 0).asObject();
        });
        colQuantity.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(cell -> {
            Drink drink = cell.getValue().getDrink();
            return new javafx.beans.property.SimpleDoubleProperty(drink != null ? drink.getPrice() : 0.0).asObject();
        });
    }

    /**
     * Configura el ComboBox de begudes i gestiona la visualització dels noms i actualització del preu.
     */
    private void setupDrinkComboBox() {
        List<Drink> drinks = drinkDAO.getAllDrinks();
        drinkComboBox.setItems(FXCollections.observableArrayList(drinks));

        drinkComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Drink drink, boolean empty) {
                super.updateItem(drink, empty);
                setText(empty || drink == null ? "" : drink.getName());
            }
        });

        drinkComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Drink drink, boolean empty) {
                super.updateItem(drink, empty);
                setText(empty || drink == null ? "" : drink.getName());
            }
        });

        drinkComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldDrink, newDrink) -> {
            priceField.setText(newDrink != null ? String.format(Locale.US, "%.2f", newDrink.getPrice()) : "");
        });
    }

    /**
     * Deshabilita tots els botons per evitar interaccions.
     */
    private void disableAllButtons() {
        addButton.setDisable(true);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        disableLineButtons(true);
    }

    /**
     * Activa o desactiva els botons relacionats amb les línies de tiquet.
     * 
     * @param disable true per deshabilitar, false per habilitar
     */
    private void disableLineButtons(boolean disable) {
        addLineButton.setDisable(disable);
        deleteLineButton.setDisable(disable);
    }

    /**
     * Omple els camps de text amb les dades del tiquet seleccionat.
     * 
     * @param ticket tiquet seleccionat
     */
    private void populateFields(Ticket ticket) {
        userIdField.setText(String.valueOf(ticket.getUserId()));
        totalField.setText(String.format(Locale.US, "%.2f", ticket.getTotal()));
        statusField.setText(ticket.getStatus());
        disableLineButtons(false);
    }

    /**
     * Neteja els camps de text dels tiquets i deshabilita els botons de línies.
     */
    private void clearFields() {
        userIdField.clear();
        totalField.clear();
        statusField.clear();
        disableLineButtons(true);
    }

    /**
     * Carrega tots els tiquets de la base de dades i els mostra a la taula.
     */
    private void loadTickets() {
        ticketList = FXCollections.observableArrayList(ticketDAO.getAllTickets());
        ticketTable.setItems(ticketList);
        statusLabel.setText("Tiquets carregats.");
    }

    /**
     * Carrega les línies de tiquet associades a un tiquet concret i les mostra a la taula.
     * 
     * @param ticketId identificador del tiquet
     */
    private void loadTicketLines(int ticketId) {
        ticketLineList = FXCollections.observableArrayList(ticketLineDAO.getLinesByTicketId(ticketId));
        ticketLinesTable.setItems(ticketLineList);
    }

    /**
     * Neteja la taula de línies de tiquet.
     */
    private void clearTicketLines() {
        if (ticketLineList != null)
            ticketLineList.clear();
        ticketLinesTable.setItems(FXCollections.observableArrayList());
    }

    /**
     * Gestiona l'acció de afegir un nou tiquet a la base de dades.
     */
    @FXML
    private void handleAddTicket() {
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            double total = Double.parseDouble(totalField.getText().trim());
            String status = statusField.getText().trim();

            if (status.isEmpty()) {
                statusLabel.setText("L'estat no pot estar buit.");
                return;
            }

            Ticket newTicket = new Ticket(0, userId, total, status, null, null);
            if (ticketDAO.insertTicket(newTicket)) {
                statusLabel.setText("Tiquet afegit correctament.");
                loadTickets();
                clearFields();
            } else {
                statusLabel.setText("Error en afegir tiquet.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("UserId o Total no vàlids.");
        }
    }

    /**
     * Gestiona l'acció d'actualitzar un tiquet existent.
     */
    @FXML
    private void handleUpdateTicket() {
        Ticket selected = ticketTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Selecciona un tiquet.");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            double total = Double.parseDouble(totalField.getText().trim());
            String status = statusField.getText().trim();

            if (status.isEmpty()) {
                statusLabel.setText("L'estat no pot estar buit.");
                return;
            }

            selected.setUserId(userId);
            selected.setTotal(total);
            selected.setStatus(status);

            if (ticketDAO.updateTicket(selected)) {
                statusLabel.setText("Tiquet actualitzat.");
                loadTickets();
                clearFields();
            } else {
                statusLabel.setText("Error en actualitzar tiquet.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("UserId o Total no vàlids.");
        }
    }

    /**
     * Gestiona l'acció d'eliminar un tiquet seleccionat.
     */
    @FXML
    private void handleDeleteTicket() {
        Ticket selected = ticketTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Selecciona un tiquet.");
            return;
        }

        if (ticketDAO.deleteTicket(selected.getTicketId())) {
            statusLabel.setText("Tiquet eliminat.");
            loadTickets();
            clearFields();
            clearTicketLines();
        } else {
            statusLabel.setText("Error en eliminar tiquet.");
        }
    }

    /**
     * Gestiona l'acció d'afegir una línia nova a un tiquet seleccionat.
     * 
     * @throws SQLException si hi ha un error a la base de dades
     */
    @FXML
    private void handleAddLine() throws SQLException {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            statusLabel.setText("Selecciona un tiquet.");
            return;
        }

        try {
            Drink drink = drinkComboBox.getValue();
            if (drink == null) {
                statusLabel.setText("Selecciona una beguda.");
                return;
            }

            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                statusLabel.setText("Quantitat ha de ser > 0.");
                return;
            }

            TicketLine line = new TicketLine(0, selectedTicket.getTicketId(), drink, quantity);
            if (ticketLineDAO.insertLine(line)) {
                statusLabel.setText("Línia afegida.");
                loadTicketLines(selectedTicket.getTicketId());
                updateTicketTotal(selectedTicket.getTicketId());
                clearLineFields();
            } else {
                statusLabel.setText("Error en afegir línia.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Quantitat no vàlida.");
        }
    }

    /**
     * Gestiona l'acció d'eliminar una línia seleccionada d'un tiquet.
     */
    @FXML
    private void handleDeleteLine() {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        TicketLine selectedLine = ticketLinesTable.getSelectionModel().getSelectedItem();
        if (selectedTicket == null || selectedLine == null) {
            statusLabel.setText("Selecciona tiquet i línia.");
            return;
        }

        if (ticketLineDAO.deleteLine(selectedLine.getTicketLineId())) {
            statusLabel.setText("Línia eliminada.");
            loadTicketLines(selectedTicket.getTicketId());
            updateTicketTotal(selectedTicket.getTicketId());
        } else {
            statusLabel.setText("Error en eliminar línia.");
        }
    }

    /**
     * Actualitza el total del tiquet sumant el preu de totes les seves línies.
     * 
     * @param ticketId id del tiquet a actualitzar
     */
    private void updateTicketTotal(int ticketId) {
        try {
            List<TicketLine> lines = ticketLineDAO.getLinesByTicketId(ticketId);
            double newTotal = 0.0;
            for (TicketLine line : lines) {
                Drink drink = line.getDrink();
                if (drink != null) {
                    newTotal += drink.getPrice() * line.getQuantity();
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
            statusLabel.setText("Error en actualitzar total.");
            e.printStackTrace();
        }
    }

    /**
     * Neteja els camps relacionats amb la línia de tiquet (beguda, quantitat i preu).
     */
    private void clearLineFields() {
        drinkComboBox.getSelectionModel().clearSelection();
        quantityField.clear();
        priceField.clear();
    }

    /**
     * Canvia la vista a la pantalla de creació de tiquets.
     */
    @FXML
    private void handleGoToCreateTicket() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TicketCreation.fxml"));
            Parent view = loader.load();

            Stage stage = (Stage) goToCreateTicketButton.getScene().getWindow();

            // Suposem que l'arrel és un BorderPane, i hi posem la vista al centre
            BorderPane rootLayout = (BorderPane) stage.getScene().getRoot();
            rootLayout.setCenter(view);

        } catch (IOException e) {
            statusLabel.setText("Error en obrir vista de creació.");
            e.printStackTrace();
        }
    }

}

