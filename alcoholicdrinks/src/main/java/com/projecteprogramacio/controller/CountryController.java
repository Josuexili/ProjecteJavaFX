package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.CountryDAO;
import com.projecteprogramacio.model.Country;
import com.projecteprogramacio.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.util.List;

public class CountryController {

    @FXML private TableView<Country> countryTable;
    @FXML private TableColumn<Country, String> colCode;
    @FXML private TableColumn<Country, String> colName;

    @FXML private TextField codeField;
    @FXML private TextField nameField;

    @FXML private Label statusLabel;

    private CountryDAO countryDAO;
    private ObservableList<Country> countryList;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            countryDAO = new CountryDAO();
        } catch (Exception e) {
            statusLabel.setText("Error en la connexió a la base de dades");
            e.printStackTrace();
            return;
        }

        colCode.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCountryCode()));
        colName.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        loadCountries();

        countryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                codeField.setText(newSel.getCountryCode());
                codeField.setDisable(true); // Codi no modificable
                nameField.setText(newSel.getName());
            }
        });
    }

    private void loadCountries() {
        List<Country> list = countryDAO.getAllCountries();
        countryList = FXCollections.observableArrayList(list);
        countryTable.setItems(countryList);
        statusLabel.setText("Països carregats");
    }

    @FXML
    private void handleAddCountry() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();

        if (code.isEmpty() || name.isEmpty()) {
            statusLabel.setText("El codi i nom no poden estar buits");
            return;
        }

        Country country = new Country(code, name);
        if (countryDAO.insertCountry(country)) {
            statusLabel.setText("País afegit correctament");
            clearFields();
            loadCountries();
        } else {
            statusLabel.setText("Error afegint el país");
        }
    }

    @FXML
    private void handleUpdateCountry() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();

        if (code.isEmpty() || name.isEmpty()) {
            statusLabel.setText("El codi i nom no poden estar buits");
            return;
        }

        Country country = new Country(code, name);
        if (countryDAO.updateCountry(country)) {
            statusLabel.setText("País actualitzat correctament");
            clearFields();
            loadCountries();
            codeField.setDisable(false);
        } else {
            statusLabel.setText("Error actualitzant el país");
        }
    }

    @FXML
    private void handleDeleteCountry() {
        Country selected = countryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Selecciona un país per eliminar");
            return;
        }

        if (countryDAO.deleteCountry(selected.getCountryCode())) {
            statusLabel.setText("País eliminat correctament");
            clearFields();
            loadCountries();
            codeField.setDisable(false);
        } else {
            statusLabel.setText("Error eliminant el país");
        }
    }

    private void clearFields() {
        codeField.clear();
        nameField.clear();
        codeField.setDisable(false);
        countryTable.getSelectionModel().clearSelection();
    }
}
