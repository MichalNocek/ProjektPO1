package com.example.projekt_po1.sceneFunction;

import com.example.projekt_po1.HelloApplication;
import com.example.projekt_po1.objects.Zabieg;
import com.example.projekt_po1.objects.Uprawa;
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
    private TableColumn<Zabieg, String> rodzajZabieguColumn;
    @FXML
    private TableColumn<Zabieg, Double> zarobekColumn;
    @FXML
    private Button addZabiegButton;
    @FXML
    private Button deleteZabiegButton;
    @FXML
    private Button backToUprawyButton;

    private Uprawa currentUprawa; // Zachowaj obiekt Uprawa

    public void setUprawa(Uprawa uprawa) {
        this.currentUprawa = uprawa;
        uprawaDetailsLabel.setText("Zabiegi dla uprawy: " + uprawa.getNazwa() + " (ID: " + uprawa.getId() + ")");
        loadZabiegiData();
    }

    @FXML
    public void initialize() {
        nazwaColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        typColumn.setCellValueFactory(new PropertyValueFactory<>("typ"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        dawkaColumn.setCellValueFactory(new PropertyValueFactory<>("dawka"));
        kosztColumn.setCellValueFactory(new PropertyValueFactory<>("koszt"));
        rodzajZabieguColumn.setCellValueFactory(new PropertyValueFactory<>("rodzajZabiegu"));
        zarobekColumn.setCellValueFactory(new PropertyValueFactory<>("zarobek"));

        kosztColumn.setCellFactory(tc -> new TableCell<Zabieg, Double>() {
            @Override
            protected void updateItem(Double koszt, boolean empty) {
                super.updateItem(koszt, empty);
                if (empty || koszt == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", koszt));
                }
            }
        });

        zarobekColumn.setCellFactory(tc -> new TableCell<Zabieg, Double>() {
            @Override
            protected void updateItem(Double zarobek, boolean empty) {
                super.updateItem(zarobek, empty);
                if (empty || zarobek == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", zarobek));
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
        if (currentUprawa != null) {
            try {
                CrudOperation crudOperation = new CrudOperation();
                List<Zabieg> zabiegi = crudOperation.getZabiegiForUprawa(currentUprawa.getId());
                zabiegiTable.getItems().setAll(zabiegi);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Błąd ładowania danych", "Nie udało się załadować zabiegów: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void addZabieg(ActionEvent event) {
        if (currentUprawa == null) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie wybrano uprawy. Nie można dodać zabiegu.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("addZabiegMenu.fxml"));
            Parent root = loader.load();
            AddZabiegMenuController addZabiegMenuController = loader.getController();
            addZabiegMenuController.setUprawaId(currentUprawa.getId());

            Stage stage = new Stage();
            stage.setTitle("Dodaj zabieg do uprawy: " + currentUprawa.getNazwa());
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadZabiegiData();
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
            alert.setHeaderText("Usuwanie zabiegu: " + selectedZabieg.getNazwa());
            alert.setContentText("Czy na pewno chcesz usunąć ten zabieg?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                CrudOperation crudOperation = new CrudOperation();
                try {

                    crudOperation.deleteZabieg(selectedZabieg.getId(), currentUprawa.getId());
                    loadZabiegiData();
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
        Stage stage = (Stage) backToUprawyButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}