package com.projecteprogramacio.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Controlador per crear i gestionar la barra de menú dinàmica segons el rol de
 * l'usuari loguejat.
 * 
 * Aquesta classe construeix un {@link MenuBar} amb diferents opcions
 * disponibles segons el rol de l'usuari (admin, worker, client). També gestiona
 * la càrrega de vistes FXML dins d'un {@link BorderPane} principal.
 * 
 * @author Josuè González
 * @version 1.0
 */
public class MenuController {

	/** Usuari que està loguejat, que determina els permisos i opcions del menú. */
	private final User loggedUser;

	/**
	 * Layout principal on es carreguen les vistes dins el center del BorderPane.
	 */
	private final BorderPane rootLayout;

	/**
	 * Crea una instància del controlador de menú.
	 * 
	 * @param loggedUser Usuari actual que ha fet login.
	 * @param rootLayout Layout principal on es carregaran les vistes.
	 */
	public MenuController(User loggedUser, BorderPane rootLayout) {
		this.loggedUser = loggedUser;
		this.rootLayout = rootLayout;
	}

	/**
	 * Crea la barra de menú amb les opcions corresponents segons el rol de
	 * l'usuari.
	 * 
	 * - Admin: veu totes les vistes disponibles. - Worker: només veu l'opció per
	 * crear tiquets. - Client: no veu cap opció.
	 * 
	 * @return Un {@link MenuBar} configurat amb els menús i opcions corresponents.
	 */
	public MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();
		Menu menuView = new Menu("Vistes");

		String role = loggedUser.getRole().toLowerCase();

		if ("admin".equals(role)) {
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
			// Client no veu cap opció de menú
		}

		menuBar.getMenus().add(menuView);
		return menuBar;
	}

	/**
	 * Carrega una vista FXML i la mostra al centre del layout principal.
	 * 
	 * @param fxmlPath Ruta del fitxer FXML a carregar.
	 */
	private void loadView(String fxmlPath) {
		try {
			Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
			rootLayout.setCenter(view);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
