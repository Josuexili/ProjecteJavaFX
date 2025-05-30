package com.projecteprogramacio.controller;

import com.projecteprogramacio.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador principal de l'aplicació.
 * <p>
 * Aquesta classe gestiona la inicialització de la finestra principal,
 * la pantalla de login i la navegació principal segons el rol de l'usuari
 * un cop autenticat.
 * </p>
 * <p>
 * Extén de {@link javafx.application.Application} per gestionar el cicle de vida
 * de l'aplicació JavaFX.
 * </p>
 * 
 * @author Josuè González
 */
public class AppController extends Application {

    private BorderPane rootLayout;
    private Stage primaryStage;
    private User loggedUser;

    /**
     * Mètode principal invocat quan s'inicia l'aplicació.
     * Inicialitza la finestra principal i mostra la vista de login.
     * 
     * @param primaryStage L'escenari principal de JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginView();
    }

    /**
     * Mostra la vista de login.
     * <p>
     * Carrega el fitxer FXML de login i estableix el controlador associat,
     * configurant l'enllaç amb aquesta classe per gestionar la sessió.
     * </p>
     */
    private void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent loginView = loader.load();

            LoginController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(loginView, 400, 300);
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assigna l'usuari autenticat i mostra el menú principal.
     * 
     * @param user Usuari autenticat.
     */
    public void setLoggedUser(User user) {
        this.loggedUser = user;
        showMainMenu();
    }

    /**
     * Mostra la vista principal de l'aplicació després de l'autenticació.
     * <p>
     * Crea el layout principal amb un menú (només per administradors) i carrega
     * la vista inicial segons el rol de l'usuari:
     * <ul>
     *   <li>admin: Vista de gestió de begudes.</li>
     *   <li>worker: Vista de creació de tiquets.</li>
     *   <li>altres: Vista d'accés denegat (si disponible).</li>
     * </ul>
     * </p>
     */
    public void showMainMenu() {
        rootLayout = new BorderPane();
        MenuController menuController = new MenuController(loggedUser, rootLayout);

        // Crear el menú només si és admin
        MenuBar menuBar = menuController.createMenuBar();
        if (menuBar != null) {
            rootLayout.setTop(menuBar);
        }

        // Carregar la vista inicial segons el rol
        try {
            String initialView;
            if ("admin".equalsIgnoreCase(loggedUser.getRole())) {
                initialView = "/view/DrinkView.fxml";
            } else if ("worker".equalsIgnoreCase(loggedUser.getRole())) {
                initialView = "/view/TicketCreation.fxml";
            } else {
                // Vista per defecte o d'error en cas de rol desconegut
                initialView = "/view/AccessDenied.fxml";
            }

            Parent view = FXMLLoader.load(getClass().getResource(initialView));
            rootLayout.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setTitle("Gestió");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
