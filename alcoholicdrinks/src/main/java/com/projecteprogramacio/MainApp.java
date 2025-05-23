package com.projecteprogramacio;

import com.projecteprogramacio.model.User;
import com.projecteprogramacio.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private BorderPane rootLayout;
    private Stage primaryStage;
    private User loggedUser;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginView();
    }

    // Mostra la finestra de login
    private void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent loginView = loader.load();

            LoginController controller = loader.getController();
            controller.setMainApp(this);  // Passem referència per comunicar

            Scene scene = new Scene(loginView, 400, 300);
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Mètode que crida el LoginController quan l'usuari ha fet login correctament
    public void setLoggedUser(User user) {
        this.loggedUser = user;
        showMainMenu();
    }

    // Mostra la finestra principal amb el menú segons el rol
    public void showMainMenu() {
        rootLayout = new BorderPane();

        MenuBar menuBar = new MenuBar();
        Menu menuView = new Menu("Vistes");

        // Elements comuns del menú
        MenuItem menuTicketItems = new MenuItem("Ticket Items");
        menuTicketItems.setOnAction(e -> loadView("/view/TicketItemView.fxml"));

        MenuItem menuDrinks = new MenuItem("Drinks");
        menuDrinks.setOnAction(e -> loadView("/view/DrinkView.fxml"));

        MenuItem menuDrinkTypes = new MenuItem("Drink Types");
        menuDrinkTypes.setOnAction(e -> loadView("/view/DrinkTypeView.fxml"));

        MenuItem menuTickets = new MenuItem("Tickets");
        menuTickets.setOnAction(e -> loadView("/view/TicketView.fxml"));

        menuView.getItems().addAll(menuTicketItems, menuDrinks, menuDrinkTypes, menuTickets);

        // Menú addicional només per admin
        if ("admin".equalsIgnoreCase(loggedUser.getRole())) {
            MenuItem menuUsers = new MenuItem("Users");
            menuUsers.setOnAction(e -> loadView("/view/UserView.fxml"));

            MenuItem menuBrands = new MenuItem("Brands");
            menuBrands.setOnAction(e -> loadView("/view/BrandView.fxml"));

            MenuItem menuCountries = new MenuItem("Countries");
            menuCountries.setOnAction(e -> loadView("/view/CountryView.fxml"));
            
            MenuItem menuSales = new MenuItem("Sales");
            menuSales.setOnAction(e -> loadView("/view/SaleView.fxml"));

            menuView.getItems().addAll(menuUsers, menuBrands, menuCountries, menuSales);
        }

        menuBar.getMenus().add(menuView);
        rootLayout.setTop(menuBar);

        // Carregar vista inicial
        loadView("/view/TicketItemView.fxml");

        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setTitle("Alcoholic Drinks Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Carrega un FXML al centre del BorderPane
    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            rootLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
