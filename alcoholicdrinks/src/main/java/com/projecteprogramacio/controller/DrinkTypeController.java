package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.DrinkTypeDAO;
import com.projecteprogramacio.model.DrinkType;
import com.projecteprogramacio.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class DrinkTypeController {

    @FXML private TableView<DrinkType> drinkTypeTable;
    @FXML private TableColumn<DrinkType, Integer> colId;
    @FXML private TableColumn<DrinkType, String> colName;

    @FXML private TextField nameField;
    @FXML private ImageView imageView;

    @FXML private Label statusLabel;

    private ObservableList<DrinkType> drinkTypeList;
    private DrinkTypeDAO drinkTypeDAO;

    private byte[] currentImageBytes;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            drinkTypeDAO = new DrinkTypeDAO();
        } catch (Exception e) {
            statusLabel.setText("Error en la connexió a la base de dades");
            e.printStackTrace();
            return;
        }

        colId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTypeId()).asObject());
        colName.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        loadDrinkTypes();

        drinkTypeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nameField.setText(newSel.getName());
                if (newSel.getImage() != null) {
                    Image img = new Image(new java.io.ByteArrayInputStream(newSel.getImage()));
                    imageView.setImage(img);
                    currentImageBytes = newSel.getImage();
                } else {
                    imageView.setImage(null);
                    currentImageBytes = null;
                }
            }
        });
    }

    private void loadDrinkTypes() {
        List<DrinkType> list = drinkTypeDAO.getAllDrinkTypes();
        drinkTypeList = FXCollections.observableArrayList(list);
        drinkTypeTable.setItems(drinkTypeList);
        statusLabel.setText("Tipus de beguda carregats");
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona una imatge");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imatges", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(nameField.getScene().getWindow());
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file)) {
                currentImageBytes = fis.readAllBytes();
                Image img = new Image(new java.io.ByteArrayInputStream(currentImageBytes));
                imageView.setImage(img);
            } catch (IOException e) {
                statusLabel.setText("Error carregant la imatge");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAddDrinkType() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            statusLabel.setText("El nom no pot estar buit");
            return;
        }

        DrinkType newType = new DrinkType(0, name, currentImageBytes);
        if (drinkTypeDAO.insertDrinkType(newType)) {
            statusLabel.setText("Tipus de beguda afegit correctament");
            clearFields();
            loadDrinkTypes();
        } else {
            statusLabel.setText("Error afegint el tipus de beguda");
        }
    }

    @FXML
    private void handleUpdateDrinkType() {
        DrinkType selected = drinkTypeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Selecciona un tipus per actualitzar");
            return;
        }

        String name = nameField.getText();
        if (name.isEmpty()) {
            statusLabel.setText("El nom no pot estar buit");
            return;
        }

        DrinkType updated = new DrinkType(selected.getTypeId(), name, currentImageBytes);
        if (drinkTypeDAO.updateDrinkType(updated)) {
            statusLabel.setText("Tipus de beguda actualitzat correctament");
            clearFields();
            loadDrinkTypes();
        } else {
            statusLabel.setText("Error actualitzant el tipus de beguda");
        }
    }

    @FXML
    private void handleDeleteDrinkType() {
        DrinkType selected = drinkTypeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Selecciona un tipus per eliminar");
            return;
        }

        if (drinkTypeDAO.deleteDrinkType(selected.getTypeId())) {
            statusLabel.setText("Tipus de beguda eliminat correctament");
            clearFields();
            loadDrinkTypes();
        } else {
            statusLabel.setText("Error eliminant el tipus de beguda");
        }
    }

    private void clearFields() {
        nameField.clear();
        imageView.setImage(null);
        currentImageBytes = null;
    }
}
