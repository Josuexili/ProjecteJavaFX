package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.SaleDAO;
import com.projecteprogramacio.model.Sale;
import com.projecteprogramacio.util.Database;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SaleController {

    @FXML private TableView<Sale> saleTable;
    @FXML private TableColumn<Sale, Integer> colSaleId;
    @FXML private TableColumn<Sale, Integer> colTicketId;
    @FXML private TableColumn<Sale, Double> colTotalAmount;
    @FXML private TableColumn<Sale, String> colCreatedAt;
    @FXML private TableColumn<Sale, Double> colCumulativeTotal;

    @FXML private TextField ticketIdField;
    @FXML private TextField totalAmountField;
    @FXML private TextField createdAtField;
    @FXML private TextField cumulativeTotalField;

    @FXML private Label statusLabel;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private ObservableList<Sale> saleList;
    private SaleDAO saleDAO;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            saleDAO = new SaleDAO();

        } catch (Exception e) {
            statusLabel.setText("Error en la connexió a la BBDD");
            e.printStackTrace();
            return;
        }

        // Configurar columnes
        colSaleId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSaleId()).asObject());
        colTicketId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTicketId()).asObject());
        colTotalAmount.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalAmount()).asObject());
        colCreatedAt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreatedAt()));
        colCumulativeTotal.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCumulativeTotal()).asObject());

        // Quan es selecciona una fila, carregar la info als camps
        saleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                ticketIdField.setText(String.valueOf(newSelection.getTicketId()));
                totalAmountField.setText(String.valueOf(newSelection.getTotalAmount()));
                createdAtField.setText(newSelection.getCreatedAt());
                cumulativeTotalField.setText(String.valueOf(newSelection.getCumulativeTotal()));
            }
        });

        // 🔽 Aquí cal afegir aquesta línia!
        loadSales();
    }


    private void loadSales() {
        List<Sale> sales = saleDAO.getAllSales();
        saleList = FXCollections.observableArrayList(sales);
        saleTable.setItems(saleList);
        statusLabel.setText("Vendes carregades.");
    }

    @FXML
    private void handleAddSale() {
        try {
            int ticketId = Integer.parseInt(ticketIdField.getText());
            double totalAmount = Double.parseDouble(totalAmountField.getText());
            String createdAt = createdAtField.getText();
            double cumulativeTotal = Double.parseDouble(cumulativeTotalField.getText());

            Sale newSale = new Sale(0, ticketId, totalAmount, createdAt, cumulativeTotal);

            boolean inserted = saleDAO.insertSale(newSale);
            if (inserted) {
                statusLabel.setText("Venda afegida correctament.");
                clearFields();
                loadSales();
            } else {
                statusLabel.setText("Error en afegir venda.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Format numèric incorrecte.");
        }
    }

    @FXML
    private void handleUpdateSale() {
        Sale selectedSale = saleTable.getSelectionModel().getSelectedItem();
        if (selectedSale == null) {
            statusLabel.setText("Selecciona una venda per actualitzar.");
            return;
        }

        try {
            int ticketId = Integer.parseInt(ticketIdField.getText());
            double totalAmount = Double.parseDouble(totalAmountField.getText());
            String createdAt = createdAtField.getText();
            double cumulativeTotal = Double.parseDouble(cumulativeTotalField.getText());

            Sale updatedSale = new Sale(selectedSale.getSaleId(), ticketId, totalAmount, createdAt, cumulativeTotal);

            boolean updated = saleDAO.updateSale(updatedSale);
            if (updated) {
                statusLabel.setText("Venda actualitzada correctament.");
                clearFields();
                loadSales();
            } else {
                statusLabel.setText("Error en actualitzar venda.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Format numèric incorrecte.");
        }
    }

    @FXML
    private void handleDeleteSale() {
        Sale selectedSale = saleTable.getSelectionModel().getSelectedItem();
        if (selectedSale == null) {
            statusLabel.setText("Selecciona una venda per eliminar.");
            return;
        }

        boolean deleted = saleDAO.deleteSale(selectedSale.getSaleId());
        if (deleted) {
            statusLabel.setText("Venda eliminada correctament.");
            clearFields();
            loadSales();
        } else {
            statusLabel.setText("Error en eliminar venda.");
        }
    }

    private void clearFields() {
        ticketIdField.clear();
        totalAmountField.clear();
        createdAtField.clear();
        cumulativeTotalField.clear();
    }
}
