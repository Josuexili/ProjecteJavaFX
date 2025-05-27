package com.projecteprogramacio.model;

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

        String role = loggedUser.getRole().toLowerCase();

        if ("admin".equals(role)) {
            // Admin pot veure tot menys CreateTicket s'ha de carregar TicketCreation.fxml
            MenuItem menuDrinks = new MenuItem("Drinks");
            menuDrinks.setOnAction(e -> loadView("/view/DrinkView.fxml"));
            menuView.getItems().add(menuDrinks);

            MenuItem menuTickets = new MenuItem("Tickets");
            menuTickets.setOnAction(e -> loadView("/view/TicketView.fxml"));
            menuView.getItems().add(menuTickets);

            MenuItem menuTicketItems = new MenuItem("Ticket Items");
            menuTicketItems.setOnAction(e -> loadView("/view/TicketItemView.fxml"));
            menuView.getItems().add(menuTicketItems);

            MenuItem menuDrinkTypes = new MenuItem("Drink Types");
            menuDrinkTypes.setOnAction(e -> loadView("/view/DrinkTypeView.fxml"));
            menuView.getItems().add(menuDrinkTypes);

            MenuItem menuCountries = new MenuItem("Countries");
            menuCountries.setOnAction(e -> loadView("/view/CountryView.fxml"));
            menuView.getItems().add(menuCountries);

            MenuItem menuBrands = new MenuItem("Brands");
            menuBrands.setOnAction(e -> loadView("/view/BrandView.fxml"));
            menuView.getItems().add(menuBrands);

            MenuItem menuSales = new MenuItem("Sales");
            menuSales.setOnAction(e -> loadView("/view/SaleView.fxml"));
            menuView.getItems().add(menuSales);

            MenuItem menuCreateTicket = new MenuItem("Create Ticket");
            menuCreateTicket.setOnAction(e -> loadView("/view/TicketCreation.fxml"));
            menuView.getItems().add(menuCreateTicket);

            MenuItem menuUsers = new MenuItem("Users");
            menuUsers.setOnAction(e -> loadView("/view/UserView.fxml"));
            menuView.getItems().add(menuUsers);

        } else if ("worker".equals(role)) {
            MenuItem menuCreateTicket = new MenuItem("Create Ticket");
            menuCreateTicket.setOnAction(e -> loadView("/view/TicketCreation.fxml"));
            menuView.getItems().add(menuCreateTicket);

        } else if ("client".equals(role)) {
            // client no veu cap opció
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
