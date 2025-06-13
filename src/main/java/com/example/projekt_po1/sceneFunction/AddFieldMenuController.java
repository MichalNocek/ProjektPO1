package com.example.projekt_po1.sceneFunction;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import jdbc.CrudOperation;

import java.sql.SQLException;

public class AddFieldMenuController {
    @FXML
    private javafx.scene.control.TextField fieldNameField;
    @FXML
    private  javafx.scene.control.TextField areaField;
    @FXML
    private javafx.scene.control.TextField locationField;

    @FXML
    public void handleAddField(ActionEvent event) {
        String name = fieldNameField.getText();
        String areaText = areaField.getText();
        String localisation = locationField.getText();

        if (name.isEmpty() || areaText.isEmpty() || localisation.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Błąd walidacji", "Wszystkie pola muszą być wypełnione!");
            return;
        }

        try {
            double area = Double.parseDouble(areaText);
            CrudOperation crudOperation = new CrudOperation();
            crudOperation.addfield(name, area, localisation);
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Pole zostało pomyślnie dodane.");

            Stage stage = (Stage) fieldNameField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd formatu", "Powierzchnia musi być liczbą!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Wystąpił błąd podczas dodawania pola: " + e.getMessage());
        } catch (IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd sesji", e.getMessage());
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}