package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.DrinkDAO;
import com.projecteprogramacio.dao.DrinkTypeDAO;
import com.projecteprogramacio.dao.BrandDAO;
import com.projecteprogramacio.model.Drink;
import com.projecteprogramacio.util.Database;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.ByteArrayInputStream;

public class DrinkController {

    @FXML private TableView<Drink> drinkTable;
    @FXML private TableColumn<Drink, Integer> colId;
    @FXML private TableColumn<Drink, String> colName;
    @FXML private TableColumn<Drink, Double> colAlcohol;
    @FXML private TableColumn<Drink, Double> colPrice;
    @FXML private TableColumn<Drink, String> colDescription;
    @FXML private TableColumn<Drink, Double> colVolume;
    @FXML private TableColumn<Drink, String> colType;     // canvi de nom per claretat
    @FXML private TableColumn<Drink, String> colBrand;    // canvi de nom per claretat
    @FXML private TableColumn<Drink, String> colCountry;
    @FXML private TableColumn<Drink, ImageView> colImage;

    @FXML private TextField nameField;
    @FXML private TextField alcoholField;
    @FXML private TextField priceField;
    @FXML private TextField descriptionField;
    @FXML private TextField volumeField;
    @FXML private TextField typeField;
    @FXML private TextField brandField;
    @FXML private TextField countryField;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private ObservableList<Drink> drinkList;
    private DrinkDAO drinkDAO;
    private DrinkTypeDAO typeDAO;
    private BrandDAO brandDAO;

    private Map<Integer, String> typeNamesMap;
    private Map<Integer, String> brandNamesMap;

    @FXML
    public void initialize() {
        Connection conn = null;
        try {
            conn = Database.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error en la connexió a la base de dades");
            return;
        }
        drinkDAO = new DrinkDAO(conn);
        typeDAO = new DrinkTypeDAO(conn);      // <-- Aquí corregit
        brandDAO = new BrandDAO(conn);

    

        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDrinkId()).asObject());
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colAlcohol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAlcoholContent()).asObject());
        colPrice.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        colVolume.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getVolume()).asObject());

        // Ara mostrem noms, no ids:
        colType.setCellValueFactory(cellData -> {
            String typeName = typeNamesMap.get(cellData.getValue().getTypeId());
            return new SimpleStringProperty(typeName != null ? typeName : "Desconegut");
        });

        colBrand.setCellValueFactory(cellData -> {
            String brandName = brandNamesMap.get(cellData.getValue().getBrandId());
            return new SimpleStringProperty(brandName != null ? brandName : "Desconegut");
        });

        colCountry.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCountryCode()));

        colImage.setCellValueFactory(cellData -> {
            byte[] imageBytes = cellData.getValue().getImage();
            if (imageBytes != null && imageBytes.length > 0) {
                Image image = new Image(new ByteArrayInputStream(imageBytes));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
                return new SimpleObjectProperty<>(imageView);
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

        drinkTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> fillFormFields(newSelection)
        );

        loadDrinks();
    }



    private void loadDrinks() {
        List<Drink> drinks = drinkDAO.getAllDrinks();
        drinkList = FXCollections.observableArrayList(drinks);
        drinkTable.setItems(drinkList);
    }

    private void fillFormFields(Drink drink) {
        if (drink != null) {
            nameField.setText(drink.getName());
            alcoholField.setText(String.valueOf(drink.getAlcoholContent()));
            priceField.setText(String.valueOf(drink.getPrice()));
            descriptionField.setText(drink.getDescription());
            volumeField.setText(String.valueOf(drink.getVolume()));
            typeField.setText(String.valueOf(drink.getTypeId()));
            brandField.setText(String.valueOf(drink.getBrandId()));
            countryField.setText(drink.getCountryCode());
        }
    }

    @FXML
    private void handleAddDrink() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                statusLabel.setText("El nom no pot estar buit.");
                return;
            }

            Drink drink = new Drink();
            drink.setName(name);
            drink.setAlcoholContent(Double.parseDouble(alcoholField.getText().trim()));
            drink.setPrice(Double.parseDouble(priceField.getText().trim()));
            drink.setDescription(descriptionField.getText().trim());
            drink.setVolume(Double.parseDouble(volumeField.getText().trim()));
            drink.setTypeId(Integer.parseInt(typeField.getText().trim()));
            drink.setBrandId(Integer.parseInt(brandField.getText().trim()));
            drink.setCountryCode(countryField.getText().trim().toUpperCase());
            drink.setImage(null);

            boolean inserted = drinkDAO.insertDrink(drink);

            if (inserted) {
                statusLabel.setText("Beguda afegida correctament.");
                clearForm();
                loadDrinks();
            } else {
                statusLabel.setText("Error en afegir la beguda.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Error: comprova els valors numèrics.");
        } catch (Exception e) {
            statusLabel.setText("Error inesperat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateDrink() {
        Drink selected = drinkTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Selecciona una beguda per editar.");
            return;
        }

        try {
            selected.setName(nameField.getText().trim());
            selected.setAlcoholContent(Double.parseDouble(alcoholField.getText().trim()));
            selected.setPrice(Double.parseDouble(priceField.getText().trim()));
            selected.setDescription(descriptionField.getText().trim());
            selected.setVolume(Double.parseDouble(volumeField.getText().trim()));
            selected.setTypeId(Integer.parseInt(typeField.getText().trim()));
            selected.setBrandId(Integer.parseInt(brandField.getText().trim()));
            selected.setCountryCode(countryField.getText().trim().toUpperCase());

            boolean updated = drinkDAO.updateDrink(selected);
            if (updated) {
                statusLabel.setText("Beguda actualitzada correctament.");
                clearForm();
                loadDrinks();
            } else {
                statusLabel.setText("Error en actualitzar la beguda.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Error: comprova els valors numèrics.");
        } catch (Exception e) {
            statusLabel.setText("Error inesperat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteDrink() {
        Drink selected = drinkTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Selecciona una beguda per esborrar.");
            return;
        }

        boolean deleted = drinkDAO.deleteDrink(selected.getDrinkId());
        if (deleted) {
            statusLabel.setText("Beguda esborrada correctament.");
            clearForm();
            loadDrinks();
        } else {
            statusLabel.setText("Error en esborrar la beguda.");
        }
    }

    private void clearForm() {
        nameField.clear();
        alcoholField.clear();
        priceField.clear();
        descriptionField.clear();
        volumeField.clear();
        typeField.clear();
        brandField.clear();
        countryField.clear();
    }
}


