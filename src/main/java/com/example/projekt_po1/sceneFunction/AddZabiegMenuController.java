package com.example.projekt_po1.sceneFunction;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jdbc.CrudOperation;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddZabiegMenuController {

    @FXML
    private TextField nazwaZabieguField;
    @FXML
    private TextField typZabieguField;
    @FXML
    private DatePicker dataZabieguPicker;
    @FXML
    private TextField dawkaField;
    @FXML
    private TextField kosztField;
    @FXML
    private Text errorText;

    @FXML
    private ComboBox<String> rodzajZabieguComboBox;
    @FXML
    private TextField zarobekField;

    private int uprawaId;

    public void setUprawaId(int uprawaId) {
        this.uprawaId = uprawaId;
    }

    @FXML
    public void initialize() {
        rodzajZabieguComboBox.getItems().addAll("STANDARDOWY", "ZBIÓR");
        rodzajZabieguComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleAddZabieg(ActionEvent event) {
        String nazwa = nazwaZabieguField.getText();
        String typ = typZabieguField.getText();
        LocalDate data = dataZabieguPicker.getValue();
        String dawka = dawkaField.getText();
        String kosztText = kosztField.getText();
        String rodzajZabiegu = rodzajZabieguComboBox.getValue();
        String zarobekText = zarobekField.getText();

        if (nazwa.isEmpty() || data == null || rodzajZabiegu == null || rodzajZabiegu.isEmpty()) {
            errorText.setText("Pola: Nazwa, Data i Rodzaj Zabiegu są obowiązkowe!");
            return;
        }

        Double koszt = null;
        if (!kosztText.isEmpty()) {
            try {
                koszt = Double.parseDouble(kosztText);
            } catch (NumberFormatException e) {
                errorText.setText("Koszt musi być liczbą!");
                return;
            }
        }

        Double zarobek = null;
        if ("ZBIÓR".equalsIgnoreCase(rodzajZabiegu)) {
            if (zarobekText.isEmpty()) {
                errorText.setText("Dla zabiegu typu 'ZBIÓR' pole Zarobek jest obowiązkowe!");
                return;
            }
            try {
                zarobek = Double.parseDouble(zarobekText);
            } catch (NumberFormatException e) {
                errorText.setText("Zarobek musi być liczbą!");
                return;
            }
        }

        if (uprawaId == 0) {
            errorText.setText("Błąd: Brak ID uprawy. Nie można dodać zabiegu.");
            return;
        }

        CrudOperation crudOperation = new CrudOperation();
        try {
            crudOperation.addZabieg(uprawaId, nazwa, typ, data, dawka, koszt, rodzajZabiegu, zarobek);
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zabieg został pomyślnie dodany.");
            Stage stage = (Stage) nazwaZabieguField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Wystąpił błąd podczas dodawania zabiegu: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd sesji", e.getMessage());
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