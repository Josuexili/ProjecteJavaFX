package com.projecteprogramacio.controller;

import com.projecteprogramacio.MainApp;
import com.projecteprogramacio.model.User;
import com.projecteprogramacio.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private MainApp mainApp;  // Referència a MainApp

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Introdueix usuari i contrasenya.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT password, role FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String role = rs.getString("role");

                // Nota: Aquí assumeixes contrasenyes en text pla. Afegir hashing per producció.
                if (password.equals(storedPassword)) {
                    User user = new User();
                    user.setUsername(username);
                    user.setRole(role);

                    statusLabel.setText("Benvingut, " + username + " (" + role + ")");

                    // Tanquem finestra login
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.close();

                    // Avisem MainApp que login ha estat correcte
                    mainApp.setLoggedUser(user);

                } else {
                    statusLabel.setText("Contrasenya incorrecta.");
                }
            } else {
                statusLabel.setText("Usuari no trobat.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error en la connexió a la BBDD.");
        }
    }
}
