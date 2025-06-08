package com.example.projekt_po1.sceneFunction;

import com.example.projekt_po1.HelloApplication;
import com.example.projekt_po1.objects.Field;
import com.example.projekt_po1.objects.SessionManager;
import javafx.event.ActionEvent;
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
    public Button logoutButton;

    @FXML
    private TableView<Field> fieldTable;
    @FXML
    private TableColumn<Field, String> nameColumn;
    @FXML
    private TableColumn<Field, Double> areaColumn;
    @FXML
    private TableColumn<Field, String> localisationColumn;
    @FXML
    private Button showUprawyButton;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        areaColumn.setCellValueFactory(new PropertyValueFactory<>("area"));
        localisationColumn.setCellValueFactory(new PropertyValueFactory<>("localisation"));

        fieldTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            deleteButton.setDisable(!isSelected);
            showUprawyButton.setDisable(!isSelected);
        });
        deleteButton.setDisable(true);
        showUprawyButton.setDisable(true);

        loadFields();
    }

    private void loadFields() {
        CrudOperation crudOperation = new CrudOperation();
        try {
            List<Field> fields = crudOperation.getFields();
            fieldTable.getItems().setAll(fields);
        } catch (SQLException e) {
            showError("Błąd bazy danych", "Nie udało się załadować pól: " + e.getMessage());
        } catch (IllegalStateException e) {
            showError("Błąd autoryzacji", e.getMessage());

        }
    }

    @FXML
    private void handleAddField(ActionEvent event) {
        try {
            // Użycie pełnej ścieżki do zasobu FXML z HelloApplication.class
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projekt_po1/addFieldMenu.fxml"));
            Parent root = loader.load();
            Scene addFieldScene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(addFieldScene);
            newStage.setTitle("Dodaj Nowe Pole");
            newStage.showAndWait(); // Czekaj, aż okno dodawania pola zostanie zamknięte

            loadFields();
        } catch (IOException e) {
            showError("Błąd", "Nie udało się otworzyć widoku dodawania pola: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteField(ActionEvent event) {
        Field selectedField = fieldTable.getSelectionModel().getSelectedItem();
        if (selectedField != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Potwierdź usunięcie");
            confirmAlert.setHeaderText("Czy na pewno chcesz usunąć pole: " + selectedField.getName() + "?");
            confirmAlert.setContentText("Usunięcie pola spowoduje również usunięcie wszystkich powiązanych upraw i zabiegów.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    CrudOperation crudOperation = new CrudOperation();
                    crudOperation.deleteField(selectedField.getId());
                    loadFields(); // Odśwież listę pól po usunięciu
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Sukces");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Pole zostało pomyślnie usunięte.");
                    successAlert.showAndWait();
                } catch (SQLException e) {
                    showError("Błąd usuwania", "Wystąpił błąd podczas usuwania pola: " + e.getMessage());
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    showError("Błąd autoryzacji", e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            showError("Błąd", "Proszę wybrać pole do usunięcia z tabeli.");
        }
    }

    @FXML
    private void handleShowUprawy(ActionEvent event) {
        Field selectedField = fieldTable.getSelectionModel().getSelectedItem();
        if (selectedField != null) {
            try {
                // Poprawiona ścieżka do FXML (z uprawaMenu.fxml na uprawa.fxml)
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projekt_po1/uprawa.fxml"));
                Parent root = loader.load();
                UprawaController uprawaController = loader.getController();

                // ZMIANA TUTAJ: Zmieniono 'setFieldDetails' na 'setFieldData'
                uprawaController.setFieldData(selectedField.getId(), selectedField.getName(),
                        selectedField.getArea(), selectedField.getLocalisation());

                Scene uprawaScene = new Scene(root);
                Stage noweOkno = new Stage();
                noweOkno.setScene(uprawaScene);
                noweOkno.setTitle("Uprawy dla pola: " + selectedField.getName());

                noweOkno.setOnHidden(event1 -> {
                    loadFields();
                });

                noweOkno.show();

            } catch (IOException e) {
                showError("Błąd", "Nie udało się otworzyć widoku upraw: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Błąd", "Proszę wybrać pole, aby wyświetlić jego uprawy.");
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
        // Użycie pełnej ścieżki do zasobu FXML z HelloApplication.class
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projekt_po1/login.fxml"));
        Parent root = loader.load();
        Scene nowaScena = new Scene(root);
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setScene(nowaScena);
        stage.setTitle("Logowanie");
        stage.show();
    }
}