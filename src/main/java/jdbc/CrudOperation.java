package jdbc;


import com.example.projekt_po1.objects.Field;
import com.example.projekt_po1.objects.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudOperation {
    // USUŃ TĄ LINIĘ: User user = new User();

    // Rejestracja użytkownika
    public int registerUser(String login, String password) throws SQLException { //
        String sqlCheck = "SELECT id FROM uzytkownicy WHERE login = ?";
        String add = "INSERT INTO uzytkownicy (login, password) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setString(1, login);
                ResultSet rs = pstmtCheck.executeQuery();
                if (rs.next()) {
                    return 0;
                }
            }


            try (PreparedStatement pstmtAdd = conn.prepareStatement(add)) {
                pstmtAdd.setString(1, login);
                pstmtAdd.setString(2, password);
                pstmtAdd.executeUpdate();
                return 1;
            }
        }
    }

    // Logowanie do aplikacji
    public int loginUser(String login, String haslo) throws SQLException {
        String sql = "SELECT id FROM uzytkownicy WHERE login = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, haslo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id"); // Zwracamy ID zalogowanego użytkownika
                }
                return 0; // Nieprawidłowy login lub hasło
            }
        }
    }

    // Dodanie pola
    public void addfield(String name, double area, String localisation) throws SQLException {
        int userId = SessionManager.getLoggedInUserId(); // <-- POBIERZ ID ZALOGOWANEGO UŻYTKOWNIKA
        if (userId == 0) {
            throw new IllegalStateException("Użytkownik nie jest zalogowany!"); // Zabezpieczenie
        }

        String sql = "INSERT INTO pola (uzytkownik_id, nazwa, powierzchnia_ha, lokalizacja) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId); // Użyj ID zalogowanego użytkownika
            pstmt.setString(2, name);
            pstmt.setDouble(3, area);
            pstmt.setString(4, localisation);
            pstmt.executeUpdate();
            System.out.print("Dodano pole");
        }
    }

    // Pobranie pól dla zalogowanego użytkownika
    public List<Field> getFields() throws SQLException {
        List<Field> fieldList = new ArrayList<>();
        int userId = SessionManager.getLoggedInUserId(); // <-- POBIERZ ID ZALOGOWANEGO UŻYTKOWNIKA
        if (userId == 0) {
            // Możesz zwrócić pustą listę lub rzucić wyjątek, w zależności od logiki
            return fieldList;
        }

        String sql = "SELECT id, nazwa, powierzchnia_ha, lokalizacja FROM pola WHERE uzytkownik_id = ?"; // Wybierz tylko potrzebne kolumny
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId); // Użyj ID zalogowanego użytkownika

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Field field = new Field();
                    field.setId(rs.getInt("id"));
                    field.setName(rs.getString("nazwa"));
                    field.setArea(rs.getDouble("powierzchnia_ha"));
                    field.setLocalisation(rs.getString("lokalizacja"));
                    // field.setUser(user); // Nie ma potrzeby ustawiać całego obiektu User tutaj
                    fieldList.add(field);
                }
            }
        }
        return fieldList;
    }

    // Nowa metoda do usuwania pola
    public void deleteField(int fieldId) throws SQLException {
        com.example.projekt_po1.objects.SessionManager SessionManager = null;
        int userId = SessionManager.getLoggedInUserId(); // Upewnij się, że pole należy do zalogowanego użytkownika
        if (userId == 0) {
            throw new IllegalStateException("Użytkownik nie jest zalogowany, nie można usunąć pola.");
        }

        String sql = "DELETE FROM pola WHERE id = ? AND uzytkownik_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fieldId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }
}