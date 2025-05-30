package com.projecteprogramacio.controller;

import com.projecteprogramacio.model.User;
import com.projecteprogramacio.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Controlador per gestionar la finestra de login.
 * Permet autenticar un usuari contra la base de dades i notifica l'estat.
 * 
 *  @author Josuè González
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private AppController mainApp;  // Referència a l'aplicació principal

    /**
     * Assigna la referència a l'aplicació principal.
     * 
     * @param appController la instància principal de l'aplicació
     */
    public void setMainApp(AppController appController) {
        this.mainApp = appController;
    }

    /**
     * Gestiona l'acció de login quan l'usuari prem el botó o fa enter.
     * Comprova que els camps no estiguin buits, valida l'usuari i contrasenya,
     * i si són correctes, passa l'usuari a l'aplicació principal i tanca la finestra.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            statusLabel.setText("Introdueix un nom d’usuari.");
            return;
        }
        if (password.isEmpty()) {
            statusLabel.setText("Introdueix la contrasenya.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT user_id, password, role FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String role = rs.getString("role");

                // Compara la contrasenya introduïda amb la de la BBDD
                if (!password.equals(storedPassword)) {
                    statusLabel.setText("Contrasenya incorrecta.");
                    return;
                }

                // Login correcte
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(username);
                user.setRole(role);

                statusLabel.setText("Benvingut, " + username + " (" + role + ")");

                // Tanquem la finestra de login
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.close();

                // Passem l'usuari loguejat a l'app principal
                mainApp.setLoggedUser(user);

            } else {
                statusLabel.setText("Usuari no trobat.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error en la connexió a la BBDD.");
        }
    }
}

