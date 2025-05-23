package com.projecteprogramacio;

import com.projecteprogramacio.controller.LoginController;
import com.projecteprogramacio.controller.MenuController;
import com.projecteprogramacio.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        rootLayout.setTop(menuController.createMenuBar());

        try {
            Parent view = FXMLLoader.load(getClass().getResource("/view/DrinkView.fxml"));
            rootLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setTitle("Drinks & Users");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

