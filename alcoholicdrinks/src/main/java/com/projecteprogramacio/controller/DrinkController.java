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

/**
 * Controlador per gestionar la vista de begudes.
 * <p>
 * Aquesta classe s'encarrega de carregar, mostrar, afegir, actualitzar i
 * eliminar begudes de la base de dades. També gestiona la taula que mostra les
 * begudes, així com els camps del formulari per introduir o modificar la
 * informació.
 * <p>
 * Utilitza mapes per relacionar tipus i marques amb els seus identificadors,
 * facilitant la conversió entre noms i ids.
 */
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

	/**
	 * Inicialitza el controlador.
	 * <p>
	 * Estableix connexió amb la base de dades i inicialitza els DAOs per begudes,
	 * tipus i marques. Carrega els mapes per a conversió ràpida entre noms i IDs,
	 * configura les columnes de la taula i carrega la llista de begudes.
	 */
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

			// Omplir formulari quan es selecciona una beguda
			drinkTable.getSelectionModel().selectedItemProperty()
					.addListener((obs, oldVal, newVal) -> fillFormFields(newVal));

			loadDrinks();

		} catch (SQLException e) {
			e.printStackTrace();
			statusLabel.setText("Error en la connexió a la base de dades.");
		}
	}

	/**
	 * Configura les columnes de la taula amb les propietats corresponents de
	 * l'objecte {@code Drink}.
	 */
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

	/**
	 * Carrega la llista de begudes de la base de dades i l'estableix a la taula.
	 */
	private void loadDrinks() {
		List<Drink> drinks = drinkDAO.getAllDrinks();
		drinkList = FXCollections.observableArrayList(drinks);
		drinkTable.setItems(drinkList);
	}

	/**
	 * Omple els camps del formulari amb les dades de la beguda seleccionada.
	 * <p>
	 * Si l'objecte {@code Drink} és {@code null}, es neteja el formulari.
	 *
	 * @param drink la beguda seleccionada o {@code null} per netejar el formulari
	 */
	private void fillFormFields(Drink drink) {
		if (drink != null) {
			nameField.setText(drink.getName());
			alcoholField.setText(String.valueOf(drink.getAlcoholContent()));
			priceField.setText(String.valueOf(drink.getPrice()));
			descriptionField.setText(drink.getDescription());
			volumeField.setText(String.valueOf(drink.getVolume()));

			String typeName = typeNamesMap.get(drink.getTypeId());
			typeField.setText(typeName != null ? typeName : "");

			String brandName = brandNamesMap.get(drink.getBrandId());
			brandField.setText(brandName != null ? brandName : "");

			countryField.setText(drink.getCountryCode());
		} else {
			clearForm();
		}
	}

	/**
	 * Gestiona l'acció d'afegir una nova beguda.
	 * <p>
	 * Recull les dades del formulari, crea o recupera els identificadors de tipus i
	 * marca, i insereix la beguda a la base de dades. Actualitza la taula i els
	 * mapes corresponents després d'afegir la beguda.
	 *
	 * @throws SQLException si hi ha un error a la base de dades
	 */
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
			drink.setTypeId(typeId);
			drink.setBrandId(brandId);
			drink.setAlcoholContent(Double.parseDouble(alcoholField.getText()));
			drink.setPrice(Double.parseDouble(priceField.getText()));
			drink.setDescription(descriptionField.getText());
			drink.setVolume(Double.parseDouble(volumeField.getText()));
			drink.setCountryCode(countryField.getText());

			drinkDAO.insertDrink(drink);
			statusLabel.setText("Beguda afegida correctament.");

			// Actualitzar mapes i llista
			typeNamesMap.putIfAbsent(typeId, typeName);
			brandNamesMap.putIfAbsent(brandId, brandName);
			typeIdsMap.putIfAbsent(typeName, typeId);
			brandIdsMap.putIfAbsent(brandName, brandId);

			loadDrinks();
			clearForm();

		} catch (NumberFormatException e) {
			statusLabel.setText("Comprova els valors numèrics.");
		}
	}

	/**
	 * Gestiona l'acció d'actualitzar la beguda seleccionada amb les dades del
	 * formulari.
	 * <p>
	 * Actualitza la base de dades i la vista després de l'operació.
	 *
	 * @throws SQLException si hi ha un error a la base de dades
	 */
	@FXML
	private void handleUpdateDrink() throws SQLException {
		Drink selected = drinkTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			statusLabel.setText("Selecciona una beguda per actualitzar.");
			return;
		}

		try {
			String typeName = typeField.getText().trim();
			String brandName = brandField.getText().trim();

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

			selected.setName(nameField.getText());
			selected.setAlcoholContent(Double.parseDouble(alcoholField.getText()));
			selected.setPrice(Double.parseDouble(priceField.getText()));
			selected.setDescription(descriptionField.getText());
			selected.setVolume(Double.parseDouble(volumeField.getText()));
			selected.setTypeId(typeId);
			selected.setBrandId(brandId);
			selected.setCountryCode(countryField.getText());

			drinkDAO.updateDrink(selected);
			statusLabel.setText("Beguda actualitzada correctament.");
			loadDrinks();
			clearForm();

		} catch (NumberFormatException e) {
			statusLabel.setText("Comprova els valors numèrics.");
		}
	}

	/**
	 * Gestiona l'acció d'eliminar la beguda seleccionada.
	 * <p>
	 * Mostra un diàleg de confirmació abans d'eliminar i actualitza la taula
	 * després.
	 *
	 * @throws SQLException si hi ha un error a la base de dades
	 */
	@FXML
	private void handleDeleteDrink() throws SQLException {
		Drink selected = drinkTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			statusLabel.setText("Selecciona una beguda per eliminar.");
			return;
		}

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmació d'eliminació");
		alert.setHeaderText("Estàs segur que vols eliminar la beguda?");
		alert.setContentText("Beguda: " + selected.getName());

		if (alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent()) {
			drinkDAO.deleteDrink(selected.getDrinkId());
			statusLabel.setText("Beguda eliminada correctament.");
			loadDrinks();
			clearForm();
		}
	}

	/**
	 * Neteja els camps del formulari.
	 */
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
