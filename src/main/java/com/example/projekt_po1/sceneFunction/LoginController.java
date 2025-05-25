package com.example.projekt_po1.sceneFunction;

import com.example.projekt_po1.HelloApplication;

import com.example.projekt_po1.objects.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jdbc.CrudOperation;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text errorText;

    @FXML
    public void zaloguj_sie(MouseEvent mouseEvent) throws SQLException, IOException {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            errorText.setText("Wypełnij wszystkie pola!");
            return;
        } else {
            CrudOperation loginCheck = new CrudOperation();
            int id = loginCheck.loginUser(login, password);

            if (id != 0) {
                SessionManager.setLoggedInUserId(id);

                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("mainUser.fxml"));
                Parent root = loader.load();
                Scene nowaScena = new Scene(root);
                Stage noweOkno = new Stage();
                noweOkno.setScene(nowaScena);
                noweOkno.setTitle("Menu główne");
                Stage aktualneOkno = (Stage) loginField.getScene().getWindow();
                aktualneOkno.close();

                noweOkno.show();

            } else {
                errorText.setText("Nieprawidłowy login lub hasło!");
            }
        }
    }




    @FXML
    public void zarejestruj_sie(MouseEvent mouseEvent) throws SQLException {
        CrudOperation loginCheck = new CrudOperation();
        String login = loginField.getText();
        String password = passwordField.getText();
        int id = loginCheck.registerUser(login, password);
        System.out.println("Przejście do rejestracji");
        if (id != 0) {
            errorText.setText("Użytkownik zarejestrowany! Teraz możesz się zalogować.");
            // Po rejestracji nie logujemy automatycznie, użytkownik musi się sam zalogować
            // Wyczyść pola formularza po udanej rejestracji
            loginField.clear();
            passwordField.clear();
        } else {
            errorText.setText("Użytkownik o podanym loginie już istnieje lub wystąpił błąd!");
        }
    }










}