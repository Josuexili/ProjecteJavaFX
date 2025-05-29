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

	private void loadDrinkTypes() {
		List<DrinkType> list = drinkTypeDAO.getAllDrinkTypes();
		drinkTypeList = FXCollections.observableArrayList(list);
		drinkTypeTable.setItems(drinkTypeList);
		statusLabel.setText("Tipus de beguda carregats: " + list.size());
	}
}
