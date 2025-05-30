package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.DrinkDAO;
import com.projecteprogramacio.dao.TicketDAO;
import com.projecteprogramacio.dao.UserDAO;
import com.projecteprogramacio.model.Drink;
import com.projecteprogramacio.model.Ticket;
import com.projecteprogramacio.model.TicketLine;
import com.projecteprogramacio.model.User;
import com.projecteprogramacio.util.Database;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador per a la vista de creació de tiquets.
 * <p>
 * Gestiona la selecció de clients, la cerca i selecció de productes,
 * l'afegit de línies a un tiquet i la finalització del tiquet.
 * També permet navegar a la vista de gestió de tiquets.
 * <p>
 * Aquest controlador connecta amb la base de dades mitjançant DAO i actualitza les
 * taules i controls de la UI de manera reactiva.
 * 
 *  @author Josuè González
 */
public class CreateTicketController {

    @FXML
    private TextField clientSearchField;

    @FXML
    private ListView<User> clientListView;

    @FXML
    private Label selectedClientLabel;

    @FXML
    private TextField productSearchField;

    @FXML
    private TableView<Drink> productTable;

    @FXML
    private TableColumn<Drink, String> colProductName;

    @FXML
    private TableColumn<Drink, Double> colProductPrice;

    @FXML
    private TableColumn<Drink, Integer> colProductQuantity;

    @FXML
    private TableColumn<Drink, Void> colAddButton;

    @FXML
    private TableView<TicketLine> ticketLineTable;

    @FXML
    private TableColumn<TicketLine, String> colLineProduct;

    @FXML
    private TableColumn<TicketLine, Integer> colLineQuantity;

    @FXML
    private TableColumn<TicketLine, Double> colLineUnitPrice;

    @FXML
    private TableColumn<TicketLine, Double> colLineSubtotal;

    @FXML
    private TableColumn<TicketLine, Void> colLineDeleteButton;

    @FXML
    private TableView<Ticket> ticketTable;

    @FXML
    private TableColumn<Ticket, Integer> colTicketId;

    @FXML
    private TableColumn<Ticket, String> colTicketStatus;

    @FXML
    private TableColumn<Ticket, Double> colTicketTotal;

    @FXML
    private Label totalLabel;

    @FXML
    private Button finishTicketButton;

    private final ObservableList<User> clients = FXCollections.observableArrayList();
    private final ObservableList<Drink> products = FXCollections.observableArrayList();
    private final ObservableList<TicketLine> ticketLines = FXCollections.observableArrayList();
    private final ObservableList<Ticket> tickets = FXCollections.observableArrayList();

    private final Map<Drink, Integer> productQuantities = new HashMap<>();

    private User selectedClient;

    private UserDAO userDAO;
    private TicketDAO ticketDAO;
    private DrinkDAO drinkDAO;

    private static final String ESTAT_CREAT = "CREAT";
    private static final String ESTAT_TANCAT = "TANCAT";

    /**
     * Inicialitza el controlador.
     * <p>
     * Estableix la connexió a la base de dades, configura les taules i controls,
     * i carrega les dades inicials.
     * 
     * @throws SQLException si hi ha algun error en la connexió o càrrega inicial
     */
    public void initialize() throws SQLException {
        connectToDatabase();
        configurarClients();
        configurarProductes();
        configurarLíniesTiquet();

        carregarClients();
        carregarProductes();
        carregarTiquets();

        clientSearchField.textProperty().addListener((obs, oldVal, newVal) -> buscarClients(newVal));
        productSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                buscarProductes(newVal);
            } catch (SQLException e) {
                mostrarError("Error cercant productes: " + e.getMessage());
            }
        });
    }

    /**
     * Estableix la connexió amb la base de dades i inicialitza els DAO.
     */
    private void connectToDatabase() {
        try {
            Connection conn = Database.getConnection();
            userDAO = new UserDAO(conn);
            ticketDAO = new TicketDAO(conn);
            drinkDAO = new DrinkDAO(conn);
        } catch (SQLException e) {
            mostrarError("No s'ha pogut connectar a la base de dades!\n" + e.getMessage());
        }
    }

    /**
     * Configura la llista de clients i el seu listener de selecció.
     */
    private void configurarClients() {
        clientListView.setItems(clients);
        clientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedClient = newVal;
            selectedClientLabel.setText(newVal != null ? "Client seleccionat: " + newVal.getUsername() : "Cap client seleccionat");
        });
    }

    /**
     * Configura la taula de productes amb les columnes i els controls de quantitat i afegit.
     */
    private void configurarProductes() {
        colProductName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        colProductPrice.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrice()).asObject());

        colProductQuantity.setCellValueFactory(cell -> new SimpleIntegerProperty(1).asObject());
        colProductQuantity.setCellFactory(tc -> new TableCell<>() {
            private final Spinner<Integer> spinner = new Spinner<>(1, 100, 1, 1);

            {
                spinner.setEditable(true);
                spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                    Drink drink = getTableView().getItems().get(getIndex());
                    if (drink != null && newValue != null && newValue > 0) {
                        productQuantities.put(drink, newValue);
                    }
                });
            }

            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Drink drink = getTableView().getItems().get(getIndex());
                    spinner.getValueFactory().setValue(productQuantities.getOrDefault(drink, 1));
                    setGraphic(spinner);
                }
            }
        });

        colAddButton.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Afegir");

            {
                btn.setOnAction(e -> {
                    Drink drink = getTableView().getItems().get(getIndex());
                    int qty = productQuantities.getOrDefault(drink, 1);
                    afegirLiniaTiquet(drink, qty);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        productTable.setItems(products);
    }

    /**
     * Configura la taula de línies de tiquet amb les columnes i el botó d'eliminació.
     */
    private void configurarLíniesTiquet() {
        colLineProduct.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDrink().getName()));
        colLineQuantity.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        colLineUnitPrice.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getDrink().getPrice()).asObject());
        colLineSubtotal.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getSubtotal()).asObject());

        colLineDeleteButton.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Eliminar");

            {
                btn.setOnAction(e -> {
                    TicketLine line = getTableView().getItems().get(getIndex());
                    ticketLines.remove(line);
                    actualitzarTotal();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        ticketLineTable.setItems(ticketLines);
    }

    /**
     * Afegeix una línia al tiquet o incrementa la quantitat si ja existeix.
     * 
     * @param drink el producte a afegir
     * @param quantitat la quantitat a afegir; ha de ser positiva
     */
    private void afegirLiniaTiquet(Drink drink, int quantitat) {
        if (quantitat <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "La quantitat ha de ser positiva.");
            return;
        }

        ticketLines.stream()
                .filter(line -> line.getDrink().equals(drink))
                .findFirst()
                .ifPresentOrElse(
                    line -> line.setQuantity(line.getQuantity() + quantitat),
                    () -> ticketLines.add(new TicketLine(drink, quantitat))
                );

        actualitzarTotal();
        ticketLineTable.refresh();
    }

    /**
     * Actualitza l'etiqueta amb el total calculat de totes les línies del tiquet.
     */
    private void actualitzarTotal() {
        double total = ticketLines.stream().mapToDouble(TicketLine::getSubtotal).sum();
        totalLabel.setText(String.format("Total: %.2f €", total));
    }

    /**
     * Carrega tots els clients des de la base de dades.
     */
    private void carregarClients() {
        try {
            clients.setAll(userDAO.getAllUsers());
        } catch (SQLException e) {
            mostrarError("Error carregant clients: " + e.getMessage());
        }
    }

    /**
     * Carrega tots els productes des de la base de dades.
     * 
     * @throws SQLException si hi ha un error a la base de dades
     */
    private void carregarProductes() throws SQLException {
        products.setAll(drinkDAO.getAllDrinks());
    }

    /**
     * Carrega tots els tiquets des de la base de dades.
     * 
     * @throws SQLException si hi ha un error a la base de dades
     */
    private void carregarTiquets() throws SQLException {
        tickets.setAll(ticketDAO.getAllTickets());
    }

    /**
     * Filtra la llista de clients segons un text de cerca.
     * 
     * @param filtre text pel qual filtrar els clients (nom o usuari)
     */
    private void buscarClients(String filtre) {
        try {
            if (filtre == null || filtre.isBlank()) {
                clients.setAll(userDAO.getAllUsers());
            } else {
                clients.setAll(userDAO.searchUsersByName(filtre));
            }
        } catch (SQLException e) {
            mostrarError("Error en la cerca de clients: " + e.getMessage());
        }
    }

    /**
     * Filtra la llista de productes segons un text de cerca.
     * 
     * @param filtre text pel qual filtrar els productes (nom)
     * @throws SQLException si hi ha un error a la base de dades
     */
    private void buscarProductes(String filtre) throws SQLException {
        if (filtre == null || filtre.isBlank()) {
            products.setAll(drinkDAO.getAllDrinks());
        } else {
            products.setAll(drinkDAO.searchDrinksByName(filtre));
        }
    }

    /**
     * Finalitza la creació del tiquet i l'insereix a la base de dades.
     * <p>
     * Comprova que hi hagi un client seleccionat i almenys una línia al tiquet.
     * Mostra missatges d'error o confirmació segons el cas.
     */
    @FXML
    private void handleFinishTicket() {
        if (selectedClient == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un client per crear el tiquet.");
            return;
        }
        if (ticketLines.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Afegeix almenys una línia al tiquet.");
            return;
        }

        try {
            Ticket ticket = new Ticket(selectedClient, ESTAT_CREAT);
            ticketLines.forEach(ticket::addLine);

            boolean saved = ticketDAO.insertTicket(ticket);
            if (saved) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Tiquet creat correctament.");
                ticketLines.clear();
                actualitzarTotal();
                carregarTiquets();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No s'ha pogut crear el tiquet.");
            }
        } catch (Exception e) {
            mostrarError("Error creant tiquet: " + e.getMessage());
        }
    }

    /**
     * Canvia la vista a la pantalla de gestió de tiquets.
     * <p>
     * Carrega la vista TicketView.fxml i la mostra en l'escenari actual.
     */
    @FXML
    private void handleGoToTickets() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TicketView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) clientSearchField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarError("No s'ha pogut carregar la vista de tiquets.");
        }
    }

    /**
     * Mostra un missatge d'error en un diàleg d'alerta.
     * 
     * @param missatge missatge d'error a mostrar
     */
    private void mostrarError(String missatge) {
        mostrarAlerta(Alert.AlertType.ERROR, missatge);
    }

    /**
     * Mostra un missatge en un diàleg d'alerta.
     * 
     * @param tipus el tipus d'alerta (INFO, WARNING, ERROR, etc.)
     * @param missatge el text del missatge
     */
    private void mostrarAlerta(Alert.AlertType tipus, String missatge) {
        Alert alert = new Alert(tipus);
        alert.setTitle("Informació");
        alert.setHeaderText(null);
        alert.setContentText(missatge);
        alert.showAndWait();
    }
}
