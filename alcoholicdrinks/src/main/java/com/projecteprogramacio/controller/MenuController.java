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

		if ("admin".equalsIgnoreCase(loggedUser.getRole())) {
			// L'admin veu tot
			MenuItem menuDrinks = new MenuItem("Drinks");
			menuDrinks.setOnAction(e -> loadView("/view/DrinkView.fxml"));
			menuView.getItems().add(menuDrinks);

			MenuItem menuTickets = new MenuItem("Tickets");
			menuTickets.setOnAction(e -> loadView("/view/TicketView.fxml"));
			menuView.getItems().add(menuTickets);

			MenuItem menuDrinkTypes = new MenuItem("Drink Types");
			menuDrinkTypes.setOnAction(e -> loadView("/view/DrinkTypeView.fxml"));
			menuView.getItems().add(menuDrinkTypes);

			MenuItem menuCountries = new MenuItem("Countries");
			menuCountries.setOnAction(e -> loadView("/view/CountryView.fxml"));
			menuView.getItems().add(menuCountries);

			MenuItem menuBrands = new MenuItem("Brands");
			menuBrands.setOnAction(e -> loadView("/view/BrandView.fxml"));
			menuView.getItems().add(menuBrands);

			MenuItem menuUsers = new MenuItem("Users");
			menuUsers.setOnAction(e -> loadView("/view/UserView.fxml"));
			menuView.getItems().add(menuUsers);

			// L'admin també pot crear tiquets
			MenuItem menuCreateTicket = new MenuItem("Crear Tiquet");
			menuCreateTicket.setOnAction(e -> loadView("/view/TicketCreation.fxml"));
			menuView.getItems().add(menuCreateTicket);

		} else if ("worker".equalsIgnoreCase(loggedUser.getRole())) {
			// Worker només pot crear tiquets
			MenuItem menuCreateTicket = new MenuItem("Crear Tiquet");
			menuCreateTicket.setOnAction(e -> loadView("/view/TicketCreation.fxml"));
			menuView.getItems().add(menuCreateTicket);
		}

		// Si vols altres rols, aquí pots posar més condicions

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
