package com.projecteprogramacio;

import com.projecteprogramacio.controller.LoginController;
import com.projecteprogramacio.controller.MenuController;
import com.projecteprogramacio.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AppController extends Application {

    private BorderPane rootLayout;
    private Stage primaryStage;
    private User loggedUser;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginView();
    }

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

    public void setLoggedUser(User user) {
        this.loggedUser = user;
        showMainMenu();
    }

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
                // Valor per defecte o error
                initialView = "/view/AccessDenied.fxml"; // opcional si tens una vista d’error
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
