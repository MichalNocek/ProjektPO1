package com.example.projekt_po1.sceneFunction;

import com.example.projekt_po1.HelloApplication;
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

    // Zmieniona nazwa metody z setFieldDetails na setFieldData, aby była spójna z tym co wcześniej działało
    public void setFieldData(int fieldId, String fieldName, double fieldArea, String fieldLocalisation) {
        this.selectedFieldId = fieldId;
        this.selectedFieldName = fieldName;
        fieldDetailsLabel.setText("Szczegóły pola: " + fieldName + " (Pow. " + fieldArea + " ha, " + fieldLocalisation + ")");
        loadUprawaData();
    }

    public void setFieldData(int fieldId, String fieldName) {
        this.selectedFieldId = fieldId;
        this.selectedFieldName = fieldName;
        fieldDetailsLabel.setText("Uprawy dla pola: " + fieldName);
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
        CrudOperation crudOperation = new CrudOperation();
        try {
            List<Uprawa> uprawy = crudOperation.getUprawyForField(selectedFieldId);
            uprawaTable.getItems().setAll(uprawy);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd podczas ładowania upraw", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addUprawa(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projekt_po1/addUprawaMenu.fxml"));
            Parent root = loader.load();

            AddUprawaMenuController addUprawaMenuController = loader.getController();
            addUprawaMenuController.setPoleId(selectedFieldId); // Metoda setPoleId zostanie dodana w AddUprawaMenuController

            Scene nowaScena = new Scene(root);
            Stage noweOkno = new Stage();
            noweOkno.setScene(nowaScena);
            noweOkno.setTitle("Dodaj nową uprawę");

            noweOkno.setOnHidden(event1 -> {
                loadUprawaData();
            });

            noweOkno.show();
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
            alert.setHeaderText("Czy na pewno chcesz usunąć tę uprawę?");
            alert.setContentText("Nazwa: " + selectedUprawa.getNazwa() + ". Usunięcie uprawy spowoduje również usunięcie wszystkich powiązanych zabiegów.");

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
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projekt_po1/zabiegiMenu.fxml"));
                Parent root = loader.load();

                ZabiegiMenuController zabiegiController = loader.getController();
                zabiegiController.setUprawaData(selectedUprawa.getId(), selectedUprawa.getNazwa()); // Metoda setUprawaData zostanie dodana w ZabiegiMenuController

                Scene zabiegiScene = new Scene(root);
                Stage noweOkno = new Stage();
                noweOkno.setScene(zabiegiScene);
                noweOkno.setTitle("Zabiegi dla uprawy: " + selectedUprawa.getNazwa());
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
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projekt_po1/mainUser.fxml"));
            Parent root = loader.load();
            Scene mainScene = new Scene(root);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("Menu główne");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd przejścia", "Nie udało się wrócić do głównego menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}