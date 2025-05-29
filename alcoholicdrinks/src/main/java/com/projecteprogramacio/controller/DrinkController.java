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

	@FXML
	private TableView<Drink> drinkTable;
	@FXML
	private TableColumn<Drink, Integer> colId;
	@FXML
	private TableColumn<Drink, String> colName;
	@FXML
	private TableColumn<Drink, Double> colAlcohol;
	@FXML
	private TableColumn<Drink, Double> colPrice;
	@FXML
	private TableColumn<Drink, String> colDescription;
	@FXML
	private TableColumn<Drink, Double> colVolume;
	@FXML
	private TableColumn<Drink, String> colType;
	@FXML
	private TableColumn<Drink, String> colBrand;
	@FXML
	private TableColumn<Drink, String> colCountry;
	@FXML
	private TableColumn<Drink, ImageView> colImage;

	@FXML
	private TextField nameField;
	@FXML
	private TextField alcoholField;
	@FXML
	private TextField priceField;
	@FXML
	private TextField descriptionField;
	@FXML
	private TextField volumeField;
	@FXML
	private TextField typeField;
	@FXML
	private TextField brandField;
	@FXML
	private TextField countryField;

	@FXML
	private Button addButton;
	@FXML
	private Button updateButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Label statusLabel;

	private ObservableList<Drink> drinkList;
	private DrinkDAO drinkDAO;
	private DrinkTypeDAO typeDAO;
	private BrandDAO brandDAO;

	// Mapes id -> nom i nom -> id per tipus i marques
	private Map<Integer, String> typeNamesMap;
	private Map<Integer, String> brandNamesMap;
	private Map<String, Integer> typeIdsMap;
	private Map<String, Integer> brandIdsMap;

	private Connection conn;

	@FXML
	public void initialize() {
		try {
			conn = Database.getConnection();

			drinkDAO = new DrinkDAO(conn);
			typeDAO = new DrinkTypeDAO(conn);
			brandDAO = new BrandDAO(conn);

			// Carregar mapes de tipus i marques
			typeNamesMap = typeDAO.getAllDrinkTypes().stream()
					.collect(Collectors.toMap(t -> t.getTypeId(), t -> t.getName()));
			brandNamesMap = brandDAO.getAllBrands().stream()
					.collect(Collectors.toMap(b -> b.getBrandId(), b -> b.getName()));

			// Invertir mapes per buscar ID a partir del nom
			typeIdsMap = typeNamesMap.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
			brandIdsMap = brandNamesMap.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

			setupTableColumns();

			drinkTable.getSelectionModel().selectedItemProperty()
					.addListener((obs, oldVal, newVal) -> fillFormFields(newVal));

			loadDrinks();

		} catch (SQLException e) {
			e.printStackTrace();
			statusLabel.setText("Error en la connexió a la base de dades.");
		}
	}

	private void setupTableColumns() {
		colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDrinkId()).asObject());
		colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		colAlcohol.setCellValueFactory(
				cellData -> new SimpleDoubleProperty(cellData.getValue().getAlcoholContent()).asObject());
		colPrice.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
		colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
		colVolume.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getVolume()).asObject());

		colType.setCellValueFactory(cellData -> {
			String name = typeNamesMap.get(cellData.getValue().getTypeId());
			return new SimpleStringProperty(name != null ? name : "Desconegut");
		});

		colBrand.setCellValueFactory(cellData -> {
			String name = brandNamesMap.get(cellData.getValue().getBrandId());
			return new SimpleStringProperty(name != null ? name : "Desconegut");
		});

		colCountry.setCellValueFactory(cellData -> {
			String country = cellData.getValue().getCountryCode();
			return new SimpleStringProperty(country != null && !country.isEmpty() ? country : "Desconegut");
		});

		colImage.setCellValueFactory(cellData -> {
			byte[] img = cellData.getValue().getImage();
			if (img != null && img.length > 0) {
				Image image = new Image(new ByteArrayInputStream(img));
				ImageView iv = new ImageView(image);
				iv.setFitWidth(60);
				iv.setFitHeight(60);
				iv.setPreserveRatio(true);
				return new SimpleObjectProperty<>(iv);
			} else {
				return new SimpleObjectProperty<>(null);
			}
		});
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

			// posar el nom si existeix, sinó deixem camp en blanc
			String typeName = typeNamesMap.get(drink.getTypeId());
			typeField.setText(typeName != null ? typeName : "");

			String brandName = brandNamesMap.get(drink.getBrandId());
			brandField.setText(brandName != null ? brandName : "");

			countryField.setText(drink.getCountryCode());
		} else {
			clearForm();
		}
	}

	@FXML
	private void handleAddDrink() throws SQLException {
	    try {
	        String name = nameField.getText().trim();
	        String typeName = typeField.getText().trim();
	        String brandName = brandField.getText().trim();

	        if (name.isEmpty() || typeName.isEmpty() || brandName.isEmpty()) {
	            statusLabel.setText("El nom, tipus i marca són obligatoris.");
	            return;
	        }

	        // Obtenim o inserim tipus i marca automàticament
	        Integer typeId = typeDAO.getOrInsert(typeName, null);
	        if (typeId == -1) {
	            statusLabel.setText("Error al crear o recuperar el tipus.");
	            return;
	        }

	        Integer brandId = brandDAO.getIdOrInsert(brandName, null);
	        if (brandId == -1) {
	            statusLabel.setText("Error al crear o recuperar la marca.");
	            return;
	        }

	        Drink drink = new Drink();
	        drink.setName(name);
	        drink.setAlcoholContent(Double.parseDouble(alcoholField.getText().trim()));
	        drink.setPrice(Double.parseDouble(priceField.getText().trim()));
	        drink.setDescription(descriptionField.getText().trim());
	        drink.setVolume(Double.parseDouble(volumeField.getText().trim()));

	        drink.setTypeId(typeId);
	        drink.setBrandId(brandId);

	        String country = countryField.getText().trim().toUpperCase();
	        drink.setCountryCode(country.isEmpty() ? null : country);

	        drink.setImage(null); // Gestiona la imatge si cal

	        if (drinkDAO.insertDrink(drink)) {
	            statusLabel.setText("Beguda afegida correctament.");
	            clearForm();
	            // Actualitzem mapes perquè el nou tipus o marca estiguin disponibles
	            refreshTypeAndBrandMaps();
	            loadDrinks();
	        } else {
	            statusLabel.setText("Error en afegir la beguda.");
	        }

	    } catch (NumberFormatException e) {
	        statusLabel.setText("Error: comprova els valors numèrics.");
	    }
	}


	@FXML
	private void handleUpdateDrink() throws SQLException {
	    Drink selected = drinkTable.getSelectionModel().getSelectedItem();
	    if (selected == null) {
	        statusLabel.setText("Selecciona una beguda per editar.");
	        return;
	    }

	    try {
	        String typeName = typeField.getText().trim();
	        String brandName = brandField.getText().trim();

	        if (typeName.isEmpty() || brandName.isEmpty()) {
	            statusLabel.setText("El tipus i la marca són obligatoris.");
	            return;
	        }

	        // Obtenim o inserim tipus i marca automàticament
	        Integer typeId = typeDAO.getOrInsert(typeName, null);
	        if (typeId == -1) {
	            statusLabel.setText("Error al crear o recuperar el tipus.");
	            return;
	        }

	        Integer brandId = brandDAO.getIdOrInsert(brandName, null);
	        if (brandId == -1) {
	            statusLabel.setText("Error al crear o recuperar la marca.");
	            return;
	        }

	        selected.setName(nameField.getText().trim());
	        selected.setAlcoholContent(Double.parseDouble(alcoholField.getText().trim()));
	        selected.setPrice(Double.parseDouble(priceField.getText().trim()));
	        selected.setDescription(descriptionField.getText().trim());
	        selected.setVolume(Double.parseDouble(volumeField.getText().trim()));

	        selected.setTypeId(typeId);
	        selected.setBrandId(brandId);

	        String country = countryField.getText().trim().toUpperCase();
	        selected.setCountryCode(country.isEmpty() ? null : country);

	        if (drinkDAO.updateDrink(selected)) {
	            statusLabel.setText("Beguda actualitzada correctament.");
	            clearForm();
	            // Actualitzem mapes perquè el nou tipus o marca estiguin disponibles
	            refreshTypeAndBrandMaps();
	            loadDrinks();
	        } else {
	            statusLabel.setText("Error en actualitzar la beguda.");
	        }

	    } catch (NumberFormatException e) {
	        statusLabel.setText("Error: comprova els valors numèrics.");
	    }
	}


	@FXML
	private void handleDeleteDrink() {
		Drink selected = drinkTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			statusLabel.setText("Selecciona una beguda per esborrar.");
			return;
		}

		if (drinkDAO.deleteDrink(selected.getDrinkId())) {
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
	private void refreshTypeAndBrandMaps() throws SQLException {
	    typeNamesMap = typeDAO.getAllDrinkTypes().stream()
	            .collect(Collectors.toMap(t -> t.getTypeId(), t -> t.getName()));
	    brandNamesMap = brandDAO.getAllBrands().stream()
	            .collect(Collectors.toMap(b -> b.getBrandId(), b -> b.getName()));

	    typeIdsMap = typeNamesMap.entrySet().stream()
	            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
	    brandIdsMap = brandNamesMap.entrySet().stream()
	            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
	}

}

