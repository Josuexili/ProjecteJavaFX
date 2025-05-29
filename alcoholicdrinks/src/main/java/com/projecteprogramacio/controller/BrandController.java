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

/**
 * Controlador per gestionar la visualització i càrrega de les marques (Brand)
 * a la interfície gràfica.
 * 
 * Aquesta classe carrega les dades des de la base de dades mitjançant el DAO
 * i les mostra en una taula (TableView).
 * 
 * @author [Josuè González]
 * @version 1.0
 */
public class BrandController {

    /** Taula on es mostren les marques. */
    @FXML private TableView<Brand> brandTable;
    
    /** Columna per mostrar l'ID de la marca. */
    @FXML private TableColumn<Brand, Integer> colBrandId;
    
    /** Columna per mostrar el nom de la marca. */
    @FXML private TableColumn<Brand, String> colName;
    
    /** Columna per mostrar el codi del país de la marca. */
    @FXML private TableColumn<Brand, String> colCountryCode;
    
    /** Label per mostrar missatges d'estat i errors. */
    @FXML private Label statusLabel;

    /** Llista observable de marques que s'usa com a font de dades per la taula. */
    private ObservableList<Brand> brandList;
    
    /** Objeto DAO per accedir a les dades de marques a la base de dades. */
    private BrandDAO brandDAO;

    /**
     * Inicialitza el controlador, configurant les columnes de la taula i
     * carregant les marques de la base de dades.
     * S'executa automàticament quan es carrega el FXML.
     */
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

    /**
     * Carrega totes les marques des de la base de dades i les assigna
     * a la taula per a la seva visualització.
     */
    private void loadBrands() {
        List<Brand> brands = brandDAO.getAllBrands();
        brandList = FXCollections.observableArrayList(brands);
        brandTable.setItems(brandList);
        statusLabel.setText("Marques carregades.");
    }
}


