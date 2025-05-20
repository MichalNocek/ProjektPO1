package com.example.projekt_po1.sceneFunction;

import com.example.projekt_po1.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    public Button addButton;

    public void dodajpole(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("addFieldMenu.fxml")); // użyj właściwej nazwy pliku
            Parent root = loader.load();
            Scene nowaScena = new Scene(root);
            Stage noweOkno = new Stage();
            noweOkno.setScene(nowaScena);
            noweOkno.setTitle("Menu główne");
            noweOkno.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setContentText("Nie udało się otworzyć nowego okna: " + e.getMessage());
            alert.showAndWait();
        }
    }
}