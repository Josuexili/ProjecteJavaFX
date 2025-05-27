package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.TicketItemDAO;
import com.projecteprogramacio.model.TicketItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TicketItemController {

    @FXML
    private TableView<TicketItem> ticketItemTable;

    @FXML
    private TableColumn<TicketItem, Integer> colItemId;

    @FXML
    private TableColumn<TicketItem, Integer> colTicketId;

    @FXML
    private TableColumn<TicketItem, Integer> colDrinkId;

    @FXML
    private TableColumn<TicketItem, Integer> colQuantity;

    @FXML
    private TableColumn<TicketItem, Double> colPrice;

    @FXML
    private TextField txtTicketId;

    @FXML
    private TextField txtDrinkId;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TextField txtPrice;

    @FXML
    private Button btnAdd, btnUpdate, btnDelete;

    private TicketItemDAO dao;

    private ObservableList<TicketItem> ticketItems;

    @FXML
    public void initialize() {
        dao = new TicketItemDAO();

        colItemId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getItemId()).asObject());
        colTicketId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTicketId()).asObject());
        colDrinkId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getDrinkId()).asObject());
        colQuantity.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());

        loadTicketItems();

        ticketItemTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showTicketItemDetails(newValue)
        );
    }

    private void loadTicketItems() {
        ticketItems = FXCollections.observableArrayList(dao.getAllTicketItems());
        ticketItemTable.setItems(ticketItems);
    }

    private void showTicketItemDetails(TicketItem item) {
        if (item != null) {
            txtTicketId.setText(String.valueOf(item.getTicketId()));
            txtDrinkId.setText(String.valueOf(item.getDrinkId()));
            txtQuantity.setText(String.valueOf(item.getQuantity()));
            txtPrice.setText(String.valueOf(item.getPrice()));
        } else {
            txtTicketId.clear();
            txtDrinkId.clear();
            txtQuantity.clear();
            txtPrice.clear();
        }
    }

    @FXML
    private void handleAdd() {
        try {
            int ticketId = Integer.parseInt(txtTicketId.getText());
            int drinkId = Integer.parseInt(txtDrinkId.getText());
            int quantity = Integer.parseInt(txtQuantity.getText());
            double price = Double.parseDouble(txtPrice.getText());

            TicketItem newItem = new TicketItem(0, ticketId, drinkId, quantity, price);
            dao.insertTicketItem(newItem);

            loadTicketItems();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numeric values.");
        }
    }

    @FXML
    private void handleUpdate() {
        TicketItem selected = ticketItemTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                int ticketId = Integer.parseInt(txtTicketId.getText());
                int drinkId = Integer.parseInt(txtDrinkId.getText());
                int quantity = Integer.parseInt(txtQuantity.getText());
                double price = Double.parseDouble(txtPrice.getText());

                selected.setTicketId(ticketId);
                selected.setDrinkId(drinkId);
                selected.setQuantity(quantity);
                selected.setPrice(price);

                dao.updateTicketItem(selected);
                loadTicketItems();
                clearFields();
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Please enter valid numeric values.");
            }
        } else {
            showAlert("Selection Error", "Please select an item to update.");
        }
    }

    @FXML
    private void handleDelete() {
        TicketItem selected = ticketItemTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.deleteTicketItem(selected.getItemId());
            loadTicketItems();
            clearFields();
        } else {
            showAlert("Selection Error", "Please select an item to delete.");
        }
    }

    private void clearFields() {
        txtTicketId.clear();
        txtDrinkId.clear();
        txtQuantity.clear();
        txtPrice.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

