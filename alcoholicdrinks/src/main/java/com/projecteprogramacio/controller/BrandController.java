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
    @FXML private Label statusLabel;

    private ObservableList<Brand> brandList;
    private BrandDAO brandDAO;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            brandDAO = new BrandDAO(conn);
        } catch (Exception e) {
            statusLabel.setText("Error en la connexió a la base de dades.");
            e.printStackTrace();
            return;
        }

        colBrandId.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getBrandId()).asObject());
        colName.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colCountryCode.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCountryCode()));

        loadBrands();
    }

    private void loadBrands() {
        List<Brand> brands = brandDAO.getAllBrands();
        brandList = FXCollections.observableArrayList(brands);
        brandTable.setItems(brandList);
        statusLabel.setText("Marques carregades.");
    }
}


