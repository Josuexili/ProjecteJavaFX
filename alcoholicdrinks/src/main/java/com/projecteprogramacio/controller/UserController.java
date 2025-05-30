package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.UserDAO;
import com.projecteprogramacio.model.User;
import com.projecteprogramacio.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador JavaFX per a la gestió d’usuaris. Aquesta classe s’encarrega de
 * mostrar la taula d’usuaris, i de gestionar les accions de crear, actualitzar
 * i eliminar usuaris.
 * 
 * @author Josuè González
 */
public class UserController {

	@FXML
	private TableView<User> userTable;
	@FXML
	private TableColumn<User, Integer> colUserId;
	@FXML
	private TableColumn<User, String> colUsername;
	@FXML
	private TableColumn<User, String> colEmail;
	@FXML
	private TableColumn<User, String> colRole;

	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private TextField emailField;
	@FXML
	private TextField roleField;

	@FXML
	private Label statusLabel;
	@FXML
	private Button addButton;
	@FXML
	private Button updateButton;
	@FXML
	private Button deleteButton;

	private ObservableList<User> userList;
	private UserDAO userDAO;

	// Guardem la contrasenya real temporalment per evitar mostrar-la
	private String currentPassword = "";

	/**
	 * Inicialitza el controlador, configurant les columnes de la taula i carregant
	 * els usuaris des de la base de dades.
	 */
	@FXML
	public void initialize() {
		try {
			Connection conn = Database.getConnection();
			userDAO = new UserDAO(conn);
		} catch (SQLException e) {
			statusLabel.setText("Error connexió a la BBDD: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		colUserId.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getUserId())
						.asObject());
		colUsername.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
		colEmail.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
		colRole.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));

		userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				populateFields(newSelection);
			} else {
				clearInputFields();
			}
		});

		loadUsers();
	}

	/**
	 * Omple els camps de text amb les dades de l’usuari seleccionat.
	 *
	 * @param user L’usuari seleccionat.
	 */
	private void populateFields(User user) {
		usernameField.setText(user.getUsername());
		passwordField.clear(); // No es mostra la contrasenya per seguretat
		emailField.setText(user.getEmail());
		roleField.setText(user.getRole());
		currentPassword = user.getPassword(); // Es guarda internament la contrasenya actual
	}

	/**
	 * Carrega tots els usuaris des de la base de dades i actualitza la taula.
	 */
	private void loadUsers() {
		try {
			List<User> users = userDAO.getAllUsers();
			userList = FXCollections.observableArrayList(users);
			userTable.setItems(userList);
			statusLabel.setText("Usuaris carregats correctament.");
		} catch (SQLException e) {
			statusLabel.setText("Error al carregar usuaris: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Gestiona l’acció d’afegir un nou usuari a la base de dades. Aquest mètode es
	 * crida quan es fa clic al botó "Afegir".
	 */
	@FXML
	private void handleAddUser() {
		String username = usernameField.getText().trim();
		String password = passwordField.getText().trim();
		String email = emailField.getText().trim();
		String role = roleField.getText().trim();

		if (username.isEmpty() || password.isEmpty() || email.isEmpty() || role.isEmpty()) {
			statusLabel.setText("Omple tots els camps!");
			return;
		}

		String now = java.time.LocalDateTime.now().toString();
		User newUser = new User(0, username, password, email, now, now, now, role);

		try {
			boolean created = userDAO.createUser(newUser);
			if (created) {
				statusLabel.setText("Usuari afegit correctament.");
				clearInputFields();
				loadUsers();
			} else {
				statusLabel.setText("Error afegint usuari.");
			}
		} catch (SQLException e) {
			statusLabel.setText("Error BBDD: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Gestiona l’acció d’actualitzar un usuari seleccionat. Aquest mètode es crida
	 * quan es fa clic al botó "Actualitzar".
	 */
	@FXML
	private void handleUpdateUser() {
		User selectedUser = userTable.getSelectionModel().getSelectedItem();
		if (selectedUser == null) {
			statusLabel.setText("Selecciona un usuari per editar.");
			return;
		}

		String username = usernameField.getText().trim();
		String password = passwordField.getText().trim();
		String email = emailField.getText().trim();
		String role = roleField.getText().trim();

		if (username.isEmpty() || email.isEmpty() || role.isEmpty()) {
			statusLabel.setText("Omple tots els camps requerits (excepte password si no canvies).");
			return;
		}

		selectedUser.setUsername(username);
		selectedUser.setEmail(email);
		selectedUser.setRole(role);

		if (!password.isEmpty()) {
			selectedUser.setPassword(password);
		} else {
			selectedUser.setPassword(currentPassword); // Es manté la contrasenya anterior
		}

		try {
			boolean updated = userDAO.updateUser(selectedUser);
			if (updated) {
				statusLabel.setText("Usuari actualitzat correctament.");
				clearInputFields();
				loadUsers();
			} else {
				statusLabel.setText("Error actualitzant usuari.");
			}
		} catch (SQLException e) {
			statusLabel.setText("Error BBDD: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Gestiona l’acció d’eliminar l’usuari seleccionat de la taula i de la base de
	 * dades. Aquest mètode es crida quan es fa clic al botó "Eliminar".
	 */
	@FXML
	private void handleDeleteUser() {
		User selectedUser = userTable.getSelectionModel().getSelectedItem();
		if (selectedUser == null) {
			statusLabel.setText("Selecciona un usuari per eliminar.");
			return;
		}

		try {
			boolean deleted = userDAO.deleteUser(selectedUser.getUserId());
			if (deleted) {
				statusLabel.setText("Usuari eliminat correctament.");
				loadUsers();
			} else {
				statusLabel.setText("Error eliminant usuari.");
			}
		} catch (SQLException e) {
			statusLabel.setText("Error BBDD: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Esborra tots els camps d’entrada i desselecciona la fila de la taula.
	 */
	private void clearInputFields() {
		usernameField.clear();
		passwordField.clear();
		emailField.clear();
		roleField.clear();
		currentPassword = "";
		userTable.getSelectionModel().clearSelection();
	}
}
