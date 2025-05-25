package com.example.projekt_po1.sceneFunction;

import com.example.projekt_po1.HelloApplication;

import com.example.projekt_po1.objects.Field;
import com.example.projekt_po1.objects.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import jdbc.CrudOperation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MainController {
    @FXML
    public Button addButton;

    @FXML
    public Button deleteButton;

    @FXML
    public Button logoutButton; // <--- Upewnij się, że masz to fx:id w FXML

    @FXML
    private TableView<Field> fieldTable;
    @FXML
    private TableColumn<Field, String> nameColumn;
    @FXML
    private TableColumn<Field, Double> areaColumn;
    @FXML
    private TableColumn<Field, String> localisationColumn;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        areaColumn.setCellValueFactory(new PropertyValueFactory<>("area"));
        localisationColumn.setCellValueFactory(new PropertyValueFactory<>("localisation"));

        // Formatowanie kolumny z powierzchnią
        areaColumn.setCellFactory(column -> new TableCell<Field, Double>() {
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

        loadTableData();
    }

    private void loadTableData() {
        CrudOperation crudOperation = new CrudOperation();
        try {
            fieldTable.getItems().clear(); // <--- WAŻNE: Wyczyść tabelę przed ponownym załadowaniem!
            List<Field> pola = crudOperation.getFields();
            fieldTable.getItems().addAll(pola);
        } catch (SQLException e) {
            showError("Błąd podczas ładowania danych", e.getMessage());
        } catch (IllegalStateException e) {
            // Obsługa przypadku, gdy użytkownik nie jest zalogowany (choć nie powinien się tu znaleźć)
            showError("Błąd sesji", e.getMessage());
        }
    }

    @FXML
    public void dodajpole(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("addFieldMenu.fxml"));
            Parent root = loader.load();
            Scene nowaScena = new Scene(root);
            Stage noweOkno = new Stage();
            noweOkno.setScene(nowaScena);
            noweOkno.setTitle("Dodaj nowe pole");

            noweOkno.setOnHidden(event -> {
                loadTableData(); // Przeładuj dane w tabeli po zamknięciu okna
            });

            noweOkno.show();
        } catch (IOException e) {
            showError("Błąd", "Nie udało się otworzyć nowego okna: " + e.getMessage());
        }
    }

    @FXML
    public void usunPole(MouseEvent mouseEvent) {
        Field selectedField = fieldTable.getSelectionModel().getSelectedItem();

        if (selectedField != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potwierdzenie usunięcia");
            alert.setHeaderText("Czy na pewno chcesz usunąć to pole?");
            alert.setContentText("Nazwa: " + selectedField.getName() + "\nLokalizacja: " + selectedField.getLocalisation());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                CrudOperation crudOperation = new CrudOperation();
                try {
                    crudOperation.deleteField(selectedField.getId());
                    loadTableData(); // Odśwież dane w tabeli po usunięciu
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Sukces");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Pole zostało pomyślnie usunięte.");
                    successAlert.showAndWait();
                } catch (SQLException e) {
                    showError("Błąd usuwania", "Wystąpił błąd podczas usuwania pola: " + e.getMessage());
                } catch (IllegalStateException e) {
                    showError("Błąd autoryzacji", e.getMessage());
                }
            }
        } else {
            showError("Błąd", "Proszę wybrać pole do usunięcia z tabeli.");
        }
    }



    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    public void wyloguj_sie(MouseEvent mouseEvent) throws IOException {

        SessionManager.logout();
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Parent root = loader.load();
        Scene nowaScena = new Scene(root);
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setScene(nowaScena);
        stage.setTitle("Logowanie");
        stage.show();


    }
}