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

/**
 * Controlador per a la gestió i visualització de la taula de països.
 * <p>
 * Carrega les dades dels països des de la base de dades i les mostra en una
 * taula amb les columnes de codi i nom del país. També mostra l'estat de la
 * càrrega a través d'una etiqueta.
 * </p>
 * 
 * @author Josuè González
 */
public class CountryController {

	@FXML
	private TableView<Country> countryTable;

	@FXML
	private TableColumn<Country, String> colCode;

	@FXML
	private TableColumn<Country, String> colName;

	@FXML
	private Label statusLabel;

	private CountryDAO countryDAO;

	private ObservableList<Country> countryList;

	/**
	 * Inicialitza el controlador.
	 * <p>
	 * Estableix la connexió amb la base de dades, configura les columnes de la
	 * taula i carrega la llista de països.
	 * </p>
	 */
	@FXML
	public void initialize() {
		try {
			Connection conn = Database.getConnection();
			countryDAO = new CountryDAO(conn);
		} catch (Exception e) {
			statusLabel.setText("Error en la connexió a la base de dades");
			e.printStackTrace();
			return;
		}

		// Configura les columnes de la taula per mostrar el codi i nom del país
		colCode.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCountryCode()));
		colName.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

		// Carrega les dades dels països
		loadCountries();
	}

	/**
	 * Carrega la llista de països des de la base de dades i l'assigna a la taula
	 * per mostrar-la.
	 */
	private void loadCountries() {
		List<Country> list = countryDAO.getAllCountries();
		countryList = FXCollections.observableArrayList(list);
		countryTable.setItems(countryList);
		statusLabel.setText("Països carregats");
	}
}
