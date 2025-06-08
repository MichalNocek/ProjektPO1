package com.example.projekt_po1.sceneFunction;

import com.example.projekt_po1.HelloApplication;
import com.example.projekt_po1.objects.Zabieg;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import jdbc.CrudOperation;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;

public class ZabiegiMenuController {

    @FXML
    private Label uprawaDetailsLabel;
    @FXML
    private TableView<Zabieg> zabiegiTable;
    @FXML
    private TableColumn<Zabieg, String> nazwaColumn;
    @FXML
    private TableColumn<Zabieg, String> typColumn;
    @FXML
    private TableColumn<Zabieg, LocalDate> dataColumn;
    @FXML
    private TableColumn<Zabieg, String> dawkaColumn;
    @FXML
    private TableColumn<Zabieg, Double> kosztColumn;
    @FXML
    private TableColumn<Zabieg, String> rodzajZabieguColumn; // Nowa kolumna
    @FXML
    private TableColumn<Zabieg, Double> zarobekColumn; // Nowa kolumna


    @FXML
    private Button addZabiegButton;
    @FXML
    private Button deleteZabiegButton;
    @FXML
    private Button backToUprawyButton;

    private int selectedUprawaId;
    private String selectedUprawaNazwa;

    // Metoda do ustawiania danych uprawy, wywoływana z UprawaController
    public void setUprawaData(int uprawaId, String uprawaNazwa) {
        this.selectedUprawaId = uprawaId;
        this.selectedUprawaNazwa = uprawaNazwa;
        uprawaDetailsLabel.setText("Zabiegi dla uprawy: " + uprawaNazwa);
        loadZabiegiData();
    }

    @FXML
    public void initialize() {
        nazwaColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        typColumn.setCellValueFactory(new PropertyValueFactory<>("typ"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        dawkaColumn.setCellValueFactory(new PropertyValueFactory<>("dawka"));
        kosztColumn.setCellValueFactory(new PropertyValueFactory<>("koszt"));
        rodzajZabieguColumn.setCellValueFactory(new PropertyValueFactory<>("rodzajZabiegu")); // Ustawienie PropertyValueFactory
        zarobekColumn.setCellValueFactory(new PropertyValueFactory<>("zarobek")); // Ustawienie PropertyValueFactory

        // Formatowanie kolumny koszt do 2 miejsc po przecinku
        kosztColumn.setCellFactory(column -> new TableCell<Zabieg, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        // Formatowanie kolumny zarobek do 2 miejsc po przecinku
        zarobekColumn.setCellFactory(column -> new TableCell<Zabieg, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        zabiegiTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            deleteZabiegButton.setDisable(!isSelected);
        });
        deleteZabiegButton.setDisable(true);
    }

    private void loadZabiegiData() {
        if (selectedUprawaId == 0) {
            System.err.println("Błąd: ID uprawy nie zostało ustawione dla ZabiegiMenuController.");
            return;
        }
        CrudOperation crudOperation = new CrudOperation();
        try {
            List<Zabieg> zabiegi = crudOperation.getZabiegiForUprawa(selectedUprawaId);
            zabiegiTable.getItems().setAll(zabiegi);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd podczas ładowania zabiegów", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addZabieg(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projekt_po1/addZabiegMenu.fxml"));
            Parent root = loader.load();

            AddZabiegMenuController addZabiegMenuController = loader.getController();
            addZabiegMenuController.setUprawaId(selectedUprawaId);

            Scene nowaScena = new Scene(root);
            Stage noweOkno = new Stage();
            noweOkno.setScene(nowaScena);
            noweOkno.setTitle("Dodaj nowy zabieg dla " + selectedUprawaNazwa);

            noweOkno.setOnHidden(event1 -> {
                loadZabiegiData(); // Odśwież tabelę po zamknięciu okna
            });

            noweOkno.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć okna dodawania zabiegu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteZabieg(ActionEvent event) {
        Zabieg selectedZabieg = zabiegiTable.getSelectionModel().getSelectedItem();
        if (selectedZabieg != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potwierdzenie usunięcia");
            alert.setHeaderText("Czy na pewno chcesz usunąć ten zabieg?");
            alert.setContentText("Nazwa: " + selectedZabieg.getNazwa() + ", Typ: " + selectedZabieg.getTyp());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                CrudOperation crudOperation = new CrudOperation();
                try {
                    crudOperation.deleteZabieg(selectedZabieg.getId(), selectedUprawaId); // Przekazujemy również uprawaId
                    loadZabiegiData(); // Odśwież dane
                    showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zabieg został pomyślnie usunięty.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Błąd usuwania", "Wystąpił błąd podczas usuwania zabiegu: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Proszę wybrać zabieg do usunięcia z tabeli.");
        }
    }


    @FXML
    private void goBackToUprawy(ActionEvent event) {
        // Zamknij bieżące okno zabiegów
        Stage stage = (Stage) backToUprawyButton.getScene().getWindow();
        stage.close();
        // Okno UprawyController zostanie odświeżone automatycznie dzięki setOnHidden w UprawaController
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}