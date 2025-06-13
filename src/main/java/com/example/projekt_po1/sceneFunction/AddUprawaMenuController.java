package com.example.projekt_po1.sceneFunction;

import com.example.projekt_po1.objects.Uprawa;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jdbc.CrudOperation;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddUprawaMenuController {

    @FXML
    private TextField nazwaUprawyField;
    @FXML
    private DatePicker dataSiewuPicker;
    @FXML
    private DatePicker dataZbioruPicker;
    @FXML
    private TextField zyskField;

    private int poleId;


    public void setPoleId(int poleId) {
        this.poleId = poleId;
    }

    @FXML
    private void addUprawa(ActionEvent event) { // Zmieniono nazwę metody
        String nazwa = nazwaUprawyField.getText();
        LocalDate dataSiewu = dataSiewuPicker.getValue();
        LocalDate dataZbioru = dataZbioruPicker.getValue();
        Double zysk = null; // Domyślnie null, jeśli pole jest puste


        if (nazwa == null || nazwa.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nazwa uprawy nie może być pusta.");
            return;
        }


        if (dataSiewu == null) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Data siewu nie może być pusta.");
            return;
        }


        String zyskText = zyskField.getText();
        if (zyskText != null && !zyskText.trim().isEmpty()) {
            try {
                zysk = Double.parseDouble(zyskText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Wprowadź poprawną wartość liczbową dla zysku.");
                return;
            }
        }

        CrudOperation crudOperation = new CrudOperation();
        try {
            crudOperation.addUprawa(poleId, nazwa, dataSiewu, dataZbioru, zysk);
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Uprawa została dodana pomyślnie.");
            // Zamknij okno po dodaniu uprawy
            Stage stage = (Stage) nazwaUprawyField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd dodawania uprawy", e.getMessage());
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