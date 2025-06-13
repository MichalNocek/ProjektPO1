package com.example.projekt_po1.sceneFunction;

import com.example.projekt_po1.HelloApplication;
import com.example.projekt_po1.objects.Field; // Dodaj ten import
import com.example.projekt_po1.objects.Uprawa;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
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

public class UprawaController {

    @FXML
    private Label fieldDetailsLabel;

    @FXML
    private TableView<Uprawa> uprawaTable;
    @FXML
    private TableColumn<Uprawa, String> nazwaUprawyColumn;
    @FXML
    private TableColumn<Uprawa, LocalDate> dataSiewuColumn;
    @FXML
    private TableColumn<Uprawa, LocalDate> dataZbioruColumn;
    @FXML
    private TableColumn<Uprawa, Double> zyskColumn;

    @FXML
    private Button addUprawaButton;
    @FXML
    private Button deleteUprawaButton;
    @FXML
    private Button showZabiegiButton;
    @FXML
    private Button backButton;

    private int selectedFieldId;
    private String selectedFieldName;

    // Metoda wywoływana z MainController.java
    public void setFieldData(int fieldId, String fieldName, double fieldArea, String fieldLocalisation) {
        this.selectedFieldId = fieldId;
        this.selectedFieldName = fieldName;
        fieldDetailsLabel.setText("Uprawy dla pola: " + fieldName + " (ID: " + fieldId + ")");
        loadUprawaData();
    }

    @FXML
    public void initialize() {
        nazwaUprawyColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        dataSiewuColumn.setCellValueFactory(new PropertyValueFactory<>("dataSiewu"));
        dataZbioruColumn.setCellValueFactory(new PropertyValueFactory<>("dataZbioru"));
        zyskColumn.setCellValueFactory(new PropertyValueFactory<>("zysk"));

        zyskColumn.setCellFactory(column -> new TableCell<Uprawa, Double>() {
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


        uprawaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            deleteUprawaButton.setDisable(!isSelected);
            showZabiegiButton.setDisable(!isSelected);
        });
        deleteUprawaButton.setDisable(true);
        showZabiegiButton.setDisable(true);
    }

    private void loadUprawaData() {
        if (selectedFieldId == 0) {
            System.err.println("Błąd: ID pola nie zostało ustawione dla UprawaController.");
            return;
        }
        try {
            CrudOperation crudOperation = new CrudOperation();
            List<Uprawa> uprawy = crudOperation.getUprawyForField(selectedFieldId);
            uprawaTable.getItems().setAll(uprawy);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania danych", "Nie udało się załadować upraw: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addUprawa(ActionEvent event) {
        if (selectedFieldId == 0) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie wybrano pola. Nie można dodać uprawy.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("addUprawaMenu.fxml"));
            Parent root = loader.load();
            AddUprawaMenuController addUprawaMenuController = loader.getController();
            addUprawaMenuController.setPoleId(selectedFieldId);

            Stage stage = new Stage();
            stage.setTitle("Dodaj uprawę do pola: " + selectedFieldName);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadUprawaData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć okna dodawania uprawy: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteUprawa(ActionEvent event) {
        Uprawa selectedUprawa = uprawaTable.getSelectionModel().getSelectedItem();
        if (selectedUprawa != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potwierdzenie usunięcia");
            alert.setHeaderText("Usuwanie uprawy: " + selectedUprawa.getNazwa());
            alert.setContentText("Czy na pewno chcesz usunąć tę uprawę oraz wszystkie związane z nią zabiegi?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                CrudOperation crudOperation = new CrudOperation();
                try {
                    crudOperation.deleteUprawa(selectedUprawa.getId());
                    loadUprawaData();
                    showAlert(Alert.AlertType.INFORMATION, "Sukces", "Uprawa została pomyślnie usunięta.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Błąd usuwania", "Wystąpił błąd podczas usuwania uprawy: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Proszę wybrać uprawę do usunięcia z tabeli.");
        }
    }

    @FXML
    private void showZabiegi(ActionEvent event) {
        Uprawa selectedUprawa = uprawaTable.getSelectionModel().getSelectedItem();
        if (selectedUprawa != null) {
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("zabiegiMenu.fxml"));
                Parent root = loader.load();
                ZabiegiMenuController zabiegiMenuController = loader.getController();
                zabiegiMenuController.setUprawa(selectedUprawa);

                Stage noweOkno = new Stage();
                noweOkno.setScene(new Scene(root));
                noweOkno.setTitle("Zabiegi dla uprawy: " + selectedUprawa.getNazwa());

                noweOkno.setOnHidden(event1 -> {
                    loadUprawaData();
                });

                noweOkno.show();

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć widoku zabiegów: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Proszę wybrać uprawę, aby wyświetlić jej zabiegi.");
        }
    }

    @FXML
    private void goBackToMain(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
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