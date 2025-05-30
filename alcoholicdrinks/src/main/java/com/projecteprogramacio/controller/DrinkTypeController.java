package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.DrinkTypeDAO;
import com.projecteprogramacio.model.DrinkType;
import com.projecteprogramacio.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.util.List;

/**
 * Controlador per a la vista de tipus de begudes.
 * Gestiona la taula amb els tipus de beguda, carrega dades des de la base de dades
 * i mostra la imatge associada al tipus seleccionat.
 * 
 *  @author Josuè González
 */
public class DrinkTypeController {

    @FXML
    private TableView<DrinkType> drinkTypeTable;

    @FXML
    private TableColumn<DrinkType, Integer> colId;

    @FXML
    private TableColumn<DrinkType, String> colName;

    @FXML
    private ImageView imageView;

    @FXML
    private Label statusLabel;

    private ObservableList<DrinkType> drinkTypeList;
    private DrinkTypeDAO drinkTypeDAO;

    /**
     * Inicialitza el controlador.
     * Estableix la connexió amb la base de dades, configura les columnes de la taula,
     * carrega els tipus de beguda i configura el comportament per mostrar la imatge
     * quan un tipus de beguda és seleccionat.
     */
    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            drinkTypeDAO = new DrinkTypeDAO(conn);
        } catch (Exception e) {
            statusLabel.setText("Error en la connexió a la base de dades");
            e.printStackTrace();
            return;
        }

        // Configurar columnes
        colId.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTypeId())
                        .asObject());
        colName.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        // Carregar dades
        loadDrinkTypes();

        // Mostrar imatge quan se selecciona un tipus
        drinkTypeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null && newSel.getImage() != null) {
                Image img = new Image(new java.io.ByteArrayInputStream(newSel.getImage()));
                imageView.setImage(img);
            } else {
                imageView.setImage(null);
            }
        });
    }

    /**
     * Carrega tots els tipus de beguda des de la base de dades i actualitza la taula.
     * També actualitza l'etiqueta d'estat amb el nombre de registres carregats.
     */
    private void loadDrinkTypes() {
        List<DrinkType> list = drinkTypeDAO.getAllDrinkTypes();
        drinkTypeList = FXCollections.observableArrayList(list);
        drinkTypeTable.setItems(drinkTypeList);
        statusLabel.setText("Tipus de beguda carregats: " + list.size());
    }
}
