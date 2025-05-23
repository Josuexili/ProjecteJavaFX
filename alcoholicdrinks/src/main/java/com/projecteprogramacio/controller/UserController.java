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

public class UserController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField roleField;

    @FXML private Label statusLabel;
    @FXML private Button addButton;
    @FXML private Button deleteButton;

    private ObservableList<User> userList;
    private UserDAO userDAO;

    @FXML
    public void initialize() {
        // Inicialitza la connexió i el DAO
        try {
            Connection conn = Database.getConnection();
            userDAO = new UserDAO(conn);
        } catch (SQLException e) {
            statusLabel.setText("Error connexió a la BBDD: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Configura les columnes
        colUserId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());

        colUsername.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        colEmail.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        colRole.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));

        loadUsers();
    }

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

        // Per simplificar, utilitzem el mateix temps per created_at, last_login, last_logout
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

    private void clearInputFields() {
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        roleField.clear();
    }
}

