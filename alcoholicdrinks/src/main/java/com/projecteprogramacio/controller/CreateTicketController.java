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

public class CreateTicketController {

    @FXML private TextField clientSearchField;
    @FXML private ListView<User> clientListView;
    @FXML private Label selectedClientLabel;

    @FXML private TextField productSearchField;
    @FXML private TableView<Drink> productTable;
    @FXML private TableColumn<Drink, String> colProductName;
    @FXML private TableColumn<Drink, Double> colProductPrice;
    @FXML private TableColumn<Drink, Integer> colProductQuantity;
    @FXML private TableColumn<Drink, Void> colAddButton;

    @FXML private TableView<TicketLine> ticketLineTable;
    @FXML private TableColumn<TicketLine, String> colLineProduct;
    @FXML private TableColumn<TicketLine, Integer> colLineQuantity;
    @FXML private TableColumn<TicketLine, Double> colLineUnitPrice;
    @FXML private TableColumn<TicketLine, Double> colLineSubtotal;
    @FXML private TableColumn<TicketLine, Void> colLineDeleteButton;

    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, Integer> colTicketId;
    @FXML private TableColumn<Ticket, String> colTicketStatus;
    @FXML private TableColumn<Ticket, Double> colTicketTotal;

    @FXML private Label totalLabel;
    @FXML private Button finishTicketButton;

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

    public void initialize() throws SQLException {
        connectToDatabase();
        configurarClients();
        configurarProductes();
        configurarLíniesTiquet();
        configurarTiquets();

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

    private void configurarClients() {
        clientListView.setItems(clients);
        clientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedClient = newVal;
            selectedClientLabel.setText(newVal != null ? "Client seleccionat: " + newVal.getUsername() : "Cap client seleccionat");
        });
    }

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

    private void configurarTiquets() {
        colTicketId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getTicketId()).asObject());
        colTicketStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));
        colTicketTotal.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTotal()).asObject());

        ticketTable.setItems(tickets);
    }

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

    private void actualitzarTotal() {
        double total = ticketLines.stream().mapToDouble(TicketLine::getSubtotal).sum();
        totalLabel.setText(String.format("Total: %.2f €", total));
    }

    private void carregarClients() {
        try {
            clients.setAll(userDAO.getAllUsers());
        } catch (SQLException e) {
            mostrarError("Error carregant clients: " + e.getMessage());
        }
    }

    private void carregarProductes() throws SQLException {
        products.setAll(drinkDAO.getAllDrinks());
    }

    private void carregarTiquets() throws SQLException {
        tickets.setAll(ticketDAO.getAllTickets());
    }

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

    private void buscarProductes(String filtre) throws SQLException {
        if (filtre == null || filtre.isBlank()) {
            products.setAll(drinkDAO.getAllDrinks());
        } else {
            products.setAll(drinkDAO.searchDrinksByName(filtre));
        }
    }

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
    
    @FXML
    private void handleGoToTickets() {
        try {
            // Carreguem la nova escena de la pantalla de tiquets
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TicketView.fxml"));
            Parent root = loader.load();

            // Obtenim la finestra actual (Stage) des de qualsevol node actual (per exemple, el botó)
            Stage stage = (Stage) finishTicketButton.getScene().getWindow();

            // Assignem la nova escena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestió de Tiquets");
            stage.show();

        } catch (IOException e) {
            mostrarError("No s'ha pogut carregar la pantalla de tiquets.\n" + e.getMessage());
        }
    }


    

    private void mostrarAlerta(Alert.AlertType tipus, String missatge) {
        Alert alert = new Alert(tipus, missatge, ButtonType.OK);
        alert.showAndWait();
    }

    private void mostrarError(String missatge) {
        mostrarAlerta(Alert.AlertType.ERROR, missatge);
    }
}

