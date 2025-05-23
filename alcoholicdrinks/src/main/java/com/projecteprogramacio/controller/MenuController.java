package com.projecteprogramacio.controller;

import com.projecteprogramacio.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MenuController {

    private final User loggedUser;
    private final BorderPane rootLayout;

    public MenuController(User loggedUser, BorderPane rootLayout) {
        this.loggedUser = loggedUser;
        this.rootLayout = rootLayout;
    }

    public MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuView = new Menu("Vistes");

        MenuItem menuDrinks = new MenuItem("Drinks");
        menuDrinks.setOnAction(e -> loadView("/view/DrinkView.fxml"));
        menuView.getItems().add(menuDrinks);

        if ("admin".equalsIgnoreCase(loggedUser.getRole())) {
            MenuItem menuUsers = new MenuItem("Users");
            menuUsers.setOnAction(e -> loadView("/view/UserView.fxml"));
            menuView.getItems().add(menuUsers);
        }

        menuBar.getMenus().add(menuView);
        return menuBar;
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            rootLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
