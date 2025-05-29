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
    @FXML private Label statusLabel;

    private CountryDAO countryDAO;
    private ObservableList<Country> countryList;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            countryDAO = new CountryDAO(conn); // Assegura’t que el DAO rep la connexió!
        } catch (Exception e) {
            statusLabel.setText("Error en la connexió a la base de dades");
            e.printStackTrace();
            return;
        }

        // Configurar les columnes de la taula
        colCode.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCountryCode()));
        colName.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        // Carrega les dades
        loadCountries();
    }

    private void loadCountries() {
        List<Country> list = countryDAO.getAllCountries();
        countryList = FXCollections.observableArrayList(list);
        countryTable.setItems(countryList);
        statusLabel.setText("Països carregats");
    }
}

