package com.projecteprogramacio.controller;

import com.projecteprogramacio.dao.DrinkDAO;
import com.projecteprogramacio.dao.TicketDAO;
import com.projecteprogramacio.dao.UserDAO;
import com.projecteprogramacio.model.Drink;
import com.projecteprogramacio.model.Ticket;
import com.projecteprogramacio.model.TicketLine;
import com.projecteprogramacio.model.User;
import com.projecteprogramacio.util.Database;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    @FXML private Label totalLabel;
    @FXML private Button finishTicketButton;

    private ObservableList<User> clients = FXCollections.observableArrayList();
    private ObservableList<Drink> products = FXCollections.observableArrayList();
    private ObservableList<TicketLine> ticketLines = FXCollections.observableArrayList();

    private User selectedClient;

    private UserDAO userDAO;
    private TicketDAO ticketDAO;

    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            userDAO = new UserDAO(conn);
            ticketDAO = new TicketDAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "No s'ha pogut connectar a la base de dades!", ButtonType.OK);
            alert.showAndWait();
            return; // Atura la inicialització si no hi ha connexió
        }

        // Configurar la llista de clients
        clientListView.setItems(clients);
        clientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedClient = newVal;
            if (newVal != null) {
                selectedClientLabel.setText("Client seleccionat: " + newVal.getUsername());
            } else {
                selectedClientLabel.setText("Cap client seleccionat");
            }
        });

        // Configurar la taula de productes
        colProductName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        colProductPrice.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrice()).asObject());

        // Columna Quantitat amb Spinner per cada fila
        colProductQuantity.setCellValueFactory(cell -> new SimpleIntegerProperty(1).asObject());
        colProductQuantity.setCellFactory(tc -> new TableCell<Drink, Integer>() {
            private final Spinner<Integer> spinner = new Spinner<>(1, 100, 1);

            {
                spinner.setEditable(true);
            }

            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(spinner);
                }
            }

            public int getQuantity() {
                return spinner.getValue();
            }
        });

        // Columna Botó Afegir producte
        colAddButton.setCellFactory(col -> new TableCell<Drink, Void>() {
            private final Button addBtn = new Button("Afegir");

            {
                addBtn.setOnAction(e -> {
                    Drink drink = getTableView().getItems().get(getIndex());
                    // Agafar la quantitat del Spinner de la mateixa fila
                    TableCell<Drink, Integer> quantityCell = (TableCell<Drink, Integer>) productTable.queryAccessibleAttribute(
                            javafx.scene.AccessibleAttribute.CELL_AT_ROW_COLUMN, getIndex(), 2);
                    int quantity = 1;
                    if (quantityCell != null) {
                        Spinner<Integer> spinner = (Spinner<Integer>) quantityCell.getGraphic();
                        if (spinner != null) {
                            quantity = spinner.getValue();
                        }
                    }
                    addTicketLine(drink, quantity);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(addBtn);
                }
            }
        });

        productTable.setItems(products);

        // Configurar la taula de línies del tiquet
        colLineProduct.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDrink().getName()));
        colLineQuantity.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        colLineUnitPrice.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getDrink().getPrice()).asObject());
        colLineSubtotal.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getSubtotal()).asObject());

        // Columna Botó Eliminar línia del tiquet
        colLineDeleteButton.setCellFactory(col -> new TableCell<TicketLine, Void>() {
            private final Button deleteBtn = new Button("Eliminar");

            {
                deleteBtn.setOnAction(e -> {
                    TicketLine line = getTableView().getItems().get(getIndex());
                    ticketLines.remove(line);
                    updateTotal();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });

        ticketLineTable.setItems(ticketLines);

        // Carregar clients i productes inicialment
        try {
            loadClients("");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadProducts("");

        // Cerca en temps real dels clients
        clientSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                loadClients(newVal);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Cerca en temps real dels productes
        productSearchField.textProperty().addListener((obs, oldVal, newVal) -> loadProducts(newVal));

        // Desactivar el botó finalitzar si no hi ha línies
        finishTicketButton.disableProperty().bind(Bindings.isEmpty(ticketLines));

    }

    private void loadClients(String filter) throws SQLException {
        List<User> users = userDAO.searchUsersByName(filter);
        clients.setAll(users);
    }

    private void loadProducts(String filter) {
        List<Drink> drinks = DrinkDAO.searchDrinksByName(filter);
        products.setAll(drinks);
    }

    private void addTicketLine(Drink drink, int quantity) {
        if (quantity <= 0) return;

        Optional<TicketLine> existingLine = ticketLines.stream()
                .filter(line -> line.getDrink().getDrinkId() == drink.getDrinkId())
                .findFirst();

        if (existingLine.isPresent()) {
            existingLine.get().setQuantity(existingLine.get().getQuantity() + quantity);
        } else {
            TicketLine newLine = new TicketLine(0, 0, drink, quantity);
            ticketLines.add(newLine);
        }

        ticketLineTable.refresh();
        updateTotal();
    }

    private void updateTotal() {
        double total = ticketLines.stream().mapToDouble(TicketLine::getSubtotal).sum();
        totalLabel.setText(String.format("Total: %.2f €", total));
    }

    @FXML
    private void handleFinishTicket() {
        if (selectedClient == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Has de seleccionar un client abans de finalitzar el ticket.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (ticketLines.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "El ticket està buit. Afegeix productes abans de finalitzar.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Crear objecte Ticket
        double total = ticketLines.stream().mapToDouble(TicketLine::getSubtotal).sum();
        Ticket ticket = new Ticket();
        ticket.setUserId(selectedClient.getUserId());
        ticket.setTotal(total);
        ticket.setStatus("CREAT");  // Estat per defecte
        ticket.setLines(ticketLines);

        // Guardar a DB
        boolean saved = ticketDAO.insertTicket(ticket);

        if (saved) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ticket guardat correctament!", ButtonType.OK);
            alert.showAndWait();
            clearAll();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error guardant el ticket. Torna-ho a intentar.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void clearAll() {
        selectedClient = null;
        selectedClientLabel.setText("Cap client seleccionat");
        clientListView.getSelectionModel().clearSelection();
        ticketLines.clear();
        updateTotal();
    }
}

