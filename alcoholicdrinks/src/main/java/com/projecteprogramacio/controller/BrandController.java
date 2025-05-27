package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.BrandDAO;
import com.projecteprogramacio.model.Brand;
import com.projecteprogramacio.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.util.List;

public class BrandController {

    @FXML private TableView<Brand> brandTable;
    @FXML private TableColumn<Brand, Integer> colBrandId;
    @FXML private TableColumn<Brand, String> colName;
    @FXML private TableColumn<Brand, String> colCountryCode;

    @FXML private TextField nameField;
    @FXML private TextField countryCodeField;

    @FXML private Label statusLabel;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private ObservableList<Brand> brandList;
    private BrandDAO brandDAO;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            brandDAO = new BrandDAO();
        } catch (Exception e) {
            statusLabel.setText("Error en la connexió a la BBDD");
            e.printStackTrace();
            return;
        }

        // Configurar columnes
        colBrandId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getBrandId()).asObject());
        colName.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colCountryCode.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCountryCode()));

        loadBrands();

        // Quan es selecciona una fila, carregar la info als camps
        brandTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                countryCodeField.setText(newSelection.getCountryCode());
            }
        });
    }

    private void loadBrands() {
        List<Brand> brands = brandDAO.getAllBrands();
        brandList = FXCollections.observableArrayList(brands);
        brandTable.setItems(brandList);
        statusLabel.setText("Marques carregades.");
    }

    @FXML
    private void handleAddBrand() {
        String name = nameField.getText();
        String countryCode = countryCodeField.getText();

        if (name.isEmpty() || countryCode.isEmpty()) {
            statusLabel.setText("Omple tots els camps.");
            return;
        }

        Brand newBrand = new Brand(0, name, countryCode);
        try {
            brandDAO.insertBrand(newBrand);
            statusLabel.setText("Marca afegida correctament.");
            clearFields();
            loadBrands();
        } catch (Exception e) {
            statusLabel.setText("Error en afegir la marca.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateBrand() {
        Brand selectedBrand = brandTable.getSelectionModel().getSelectedItem();
        if (selectedBrand == null) {
            statusLabel.setText("Selecciona una marca per actualitzar.");
            return;
        }

        String name = nameField.getText();
        String countryCode = countryCodeField.getText();

        if (name.isEmpty() || countryCode.isEmpty()) {
            statusLabel.setText("Omple tots els camps.");
            return;
        }

        Brand updatedBrand = new Brand(selectedBrand.getBrandId(), name, countryCode);
        try {
            brandDAO.updateBrand(updatedBrand);
            statusLabel.setText("Marca actualitzada correctament.");
            clearFields();
            loadBrands();
        } catch (Exception e) {
            statusLabel.setText("Error en actualitzar la marca.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteBrand() {
        Brand selectedBrand = brandTable.getSelectionModel().getSelectedItem();
        if (selectedBrand == null) {
            statusLabel.setText("Selecciona una marca per eliminar.");
            return;
        }

        try {
            brandDAO.deleteBrand(selectedBrand.getBrandId());
            statusLabel.setText("Marca eliminada correctament.");
            clearFields();
            loadBrands();
        } catch (Exception e) {
            statusLabel.setText("Error en eliminar la marca.");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.clear();
        countryCodeField.clear();
    }
}

