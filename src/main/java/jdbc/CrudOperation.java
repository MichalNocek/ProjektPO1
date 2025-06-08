package jdbc;

import com.example.projekt_po1.objects.Field;
import com.example.projekt_po1.objects.SessionManager;
import com.example.projekt_po1.objects.Uprawa;
import com.example.projekt_po1.objects.Zabieg; // DODAJ TEN IMPORT

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types; // DODAJ TEN IMPORT
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CrudOperation {

    // Rejestracja użytkownika
    public int registerUser(String login, String password) throws SQLException {
        String sqlCheck = "SELECT id FROM uzytkownicy WHERE login = ?";
        String add = "INSERT INTO uzytkownicy (login, password) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setString(1, login);
                ResultSet rs = pstmtCheck.executeQuery();
                if (rs.next()) {
                    return 0; // Użytkownik już istnieje
                }
            }

            try (PreparedStatement pstmtAdd = conn.prepareStatement(add)) {
                pstmtAdd.setString(1, login);
                pstmtAdd.setString(2, password);
                pstmtAdd.executeUpdate();
                return 1; // Użytkownik zarejestrowany
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
                    return rs.getInt("id"); // Zwróć ID zalogowanego użytkownika
                }
                return 0; // Nie znaleziono użytkownika
            }
        }
    }

    // Dodanie pola
    public void addfield(String name, double area, String localisation) throws SQLException {
        int userId = SessionManager.getLoggedInUserId();
        if (userId == 0) {
            throw new IllegalStateException("Użytkownik nie jest zalogowany!");
        }

        String sql = "INSERT INTO pola (uzytkownik_id, nazwa, powierzchnia_ha, lokalizacja) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
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
        int userId = SessionManager.getLoggedInUserId();
        if (userId == 0) {
            throw new IllegalStateException("Użytkownik nie jest zalogowany, nie można pobrać pól.");
        }

        String sql = "SELECT id, nazwa, powierzchnia_ha, lokalizacja FROM pola WHERE uzytkownik_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Field field = new Field();
                    field.setId(rs.getInt("id"));
                    field.setName(rs.getString("nazwa"));
                    field.setArea(rs.getDouble("powierzchnia_ha"));
                    field.setLocalisation(rs.getString("lokalizacja"));
                    fieldList.add(field);
                }
            }
        }
        return fieldList;
    }

    // Metoda do usuwania pola
    public void deleteField(int fieldId) throws SQLException {
        int userId = SessionManager.getLoggedInUserId(); // Upewnij się, że pole należy do zalogowanego użytkownika
        if (userId == 0) {
            throw new IllegalStateException("Użytkownik nie jest zalogowany, nie można usunąć pola.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Rozpoczynamy transakcję

            // 1. Usuń wszystkie zabiegi powiązane z uprawami na tym polu
            String sqlDeleteZabiegi = "DELETE FROM zabiegi WHERE uprawa_id IN (SELECT id FROM uprawy WHERE pole_id = ?)";
            try (PreparedStatement pstmtDeleteZabiegi = conn.prepareStatement(sqlDeleteZabiegi)) {
                pstmtDeleteZabiegi.setInt(1, fieldId);
                pstmtDeleteZabiegi.executeUpdate();
            }

            // 2. Usuń wszystkie uprawy powiązane z tym polem
            String sqlDeleteUprawy = "DELETE FROM uprawy WHERE pole_id = ?";
            try (PreparedStatement pstmtDeleteUprawy = conn.prepareStatement(sqlDeleteUprawy)) {
                pstmtDeleteUprawy.setInt(1, fieldId);
                pstmtDeleteUprawy.executeUpdate();
            }

            // 3. Usuń samo pole
            String sqlDeleteField = "DELETE FROM pola WHERE id = ? AND uzytkownik_id = ?";
            try (PreparedStatement pstmtDeleteField = conn.prepareStatement(sqlDeleteField)) {
                pstmtDeleteField.setInt(1, fieldId);
                pstmtDeleteField.setInt(2, userId);
                pstmtDeleteField.executeUpdate();
            }

            conn.commit(); // Zatwierdzamy transakcję, jeśli wszystko poszło dobrze
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Wycofujemy transakcję w razie błędu
            }
            throw e; // Przekazujemy wyjątek dalej
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Przywracamy domyślny tryb auto-commit
                conn.close(); // Zamykamy połączenie
            }
        }
    }

    // Dodawanie nowej uprawy z opcjonalnymi datą zbioru i zyskiem
    public void addUprawa(int poleId, String nazwa, LocalDate dataSiewu, LocalDate dataZbioru, Double zysk) throws SQLException {
        String sql = "INSERT INTO uprawy (pole_id, nazwa, data_siewu, data_zbioru, zysk) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, poleId);
            pstmt.setString(2, nazwa);
            pstmt.setDate(3, java.sql.Date.valueOf(dataSiewu));

            if (dataZbioru != null) {
                pstmt.setDate(4, java.sql.Date.valueOf(dataZbioru));
            } else {
                pstmt.setNull(4, java.sql.Types.DATE);
            }

            // Używamy Types.REAL, ponieważ kolumna zysk jest float4 w PostgreSQL
            if (zysk != null) {
                pstmt.setDouble(5, zysk);
            } else {
                pstmt.setNull(5, Types.REAL); // Typ danych REAL w PostgreSQL
            }

            pstmt.executeUpdate();
            System.out.println("Dodano uprawę dla pola ID: " + poleId);
        }
    }

    // Pobieranie upraw dla konkretnego pola
    public List<Uprawa> getUprawyForField(int poleId) throws SQLException {
        List<Uprawa> uprawaList = new ArrayList<>();
        String sql = "SELECT id, nazwa, data_siewu, data_zbioru, zysk FROM uprawy WHERE pole_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, poleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Uprawa uprawa = new Uprawa();
                    uprawa.setId(rs.getInt("id"));
                    uprawa.setPoleId(poleId);
                    uprawa.setNazwa(rs.getString("nazwa"));
                    uprawa.setDataSiewu(rs.getDate("data_siewu") != null ? rs.getDate("data_siewu").toLocalDate() : null);
                    uprawa.setDataZbioru(rs.getDate("data_zbioru") != null ? rs.getDate("data_zbioru").toLocalDate() : null);

                    // PRAWIDŁOWA ZMIANA TUTAJ DLA "ZYSK": Odczytujemy jako float, następnie konwertujemy na Double
                    float retrievedZyskFloat = rs.getFloat("zysk");
                    if (rs.wasNull()) {
                        uprawa.setZysk(0.0); // Ustaw 0.0 jeśli zysk jest NULL w bazie
                    } else {
                        uprawa.setZysk((double) retrievedZyskFloat); // Konwertuj float na double
                    }
                    uprawaList.add(uprawa);
                }
            }
        }
        return uprawaList;
    }

    // Usuwanie uprawy (zakłada ON DELETE CASCADE na zabiegach, jeśli nie to najpierw usuń zabiegi)
    public void deleteUprawa(int uprawaId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Rozpoczynamy transakcję

            // Najpierw usuwamy wszystkie zabiegi powiązane z tą uprawą
            String sqlDeleteZabiegi = "DELETE FROM zabiegi WHERE uprawa_id = ?";
            try (PreparedStatement pstmtDeleteZabiegi = conn.prepareStatement(sqlDeleteZabiegi)) {
                pstmtDeleteZabiegi.setInt(1, uprawaId);
                pstmtDeleteZabiegi.executeUpdate();
            }

            // Następnie usuwamy samą uprawę
            String sqlDeleteUprawa = "DELETE FROM uprawy WHERE id = ?";
            try (PreparedStatement pstmtDeleteUprawa = conn.prepareStatement(sqlDeleteUprawa)) {
                pstmtDeleteUprawa.setInt(1, uprawaId);
                pstmtDeleteUprawa.executeUpdate();
            }
            conn.commit(); // Zatwierdzamy transakcję
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Wycofujemy transakcję w razie błędu
            }
            throw e; // Przekazujemy wyjątek dalej
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Przywracamy domyślny tryb auto-commit
                conn.close(); // Zamykamy połączenie
            }
        }
    }

    // NOWE METODY DLA ZABIEGÓW (dostosowane do Twojego schematu DB)

    // Dodawanie nowego zabiegu
    public void addZabieg(int uprawaId, String nazwa, String typ, LocalDate data, String dawka, Double koszt, String rodzajZabiegu, Double zarobek) throws SQLException {
        String sql = "INSERT INTO zabiegi (uprawa_id, nazwa, typ, data, dawka, koszt, rodzaj_zabiegu, zarobek) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, uprawaId);

            // Nazwa (może być null w DB)
            if (nazwa != null && !nazwa.isEmpty()) {
                pstmt.setString(2, nazwa);
            } else {
                pstmt.setNull(2, Types.VARCHAR);
            }

            // Typ (może być null w DB)
            if (typ != null && !typ.isEmpty()) {
                pstmt.setString(3, typ);
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }

            // Data (może być null w DB)
            if (data != null) {
                pstmt.setDate(4, java.sql.Date.valueOf(data));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            // Dawka (może być null w DB)
            if (dawka != null && !dawka.isEmpty()) {
                pstmt.setString(5, dawka);
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }

            // Koszt (może być null w DB, używamy Double)
            if (koszt != null) {
                pstmt.setDouble(6, koszt);
            } else {
                pstmt.setNull(6, Types.REAL); // Typ danych REAL w PostgreSQL
            }
            if (rodzajZabiegu != null && !rodzajZabiegu.isEmpty()) {
                pstmt.setString(7, rodzajZabiegu);
            } else {
                pstmt.setNull(7, Types.VARCHAR);
            }
            if (zarobek != null) {
                pstmt.setDouble(8, zarobek);
            } else {
                pstmt.setNull(8, Types.REAL);
            }

            pstmt.executeUpdate();
            System.out.println("Dodano zabieg dla uprawy ID: " + uprawaId);

            if ("ZBIÓR".equalsIgnoreCase(rodzajZabiegu) && data != null) {
                updateUprawaDataZbioru(uprawaId, data);
            }
            recalculateUprawaProfit(uprawaId); // Dodano wywołanie do przeliczenia zysku uprawy
        }
    }

    // Pobieranie zabiegów dla konkretnej uprawy
    public List<Zabieg> getZabiegiForUprawa(int uprawaId) throws SQLException {
        List<Zabieg> zabiegList = new ArrayList<>();
        String sql = "SELECT id, nazwa, typ, data, dawka, koszt, rodzaj_zabiegu, zarobek FROM zabiegi WHERE uprawa_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, uprawaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // PRAWIDŁOWA ZMIANA TUTAJ DLA "KOSZT": Odczytujemy jako float, następnie konwertujemy na Double
                    float retrievedKosztFloat = rs.getFloat("koszt");
                    Double finalKoszt = rs.wasNull() ? 0.0 : (double) retrievedKosztFloat;

                    double retrievedZarobek = rs.getDouble("zarobek");
                    boolean zarobekWasNull = rs.wasNull(); // Sprawdź, czy zarobek był NULL w bazie

                    Zabieg zabieg = new Zabieg(
                            rs.getInt("id"),
                            rs.getString("nazwa"),
                            rs.getString("typ"),
                            rs.getDate("data") != null ? rs.getDate("data").toLocalDate() : null,
                            rs.getString("dawka"),
                            finalKoszt, // Użyj poprawionej wartości
                            rs.getString("rodzaj_zabiegu"),
                            zarobekWasNull ? 0.0 : retrievedZarobek, // Ustaw 0.0 jeśli zarobek był NULL
                            uprawaId
                    );
                    zabiegList.add(zabieg);
                }
            }
        }
        return zabiegList;
    }

    // Usuwanie zabiegu
    public void deleteZabieg(int zabiegId, int uprawaId) throws SQLException {
        String sql = "DELETE FROM zabiegi WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, zabiegId);
            pstmt.executeUpdate();
            recalculateUprawaProfit(uprawaId); // Po usunięciu zabiegu przelicz zysk uprawy
        }
    }

    // Obliczanie całkowitych kosztów dla danej uprawy
    public double getTotalKosztForUprawa(int uprawaId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(koszt), 0.0) AS total_koszt FROM zabiegi WHERE uprawa_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, uprawaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Odczytujemy jako float, następnie konwertujemy na Double
                    float totalKosztFloat = rs.getFloat("total_koszt");
                    return (double) totalKosztFloat;
                }
            }
        }
        return 0.0;
    }

    // Obliczanie całkowitych zarobków dla danej uprawy (tylko zabiegi typu 'ZBIÓR')
    public double getTotalZarobekForUprawa(int uprawaId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(zarobek), 0.0) AS total_zarobek FROM zabiegi WHERE uprawa_id = ? AND UPPER(rodzaj_zabiegu) = 'ZBIÓR'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, uprawaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Odczytujemy jako float, następnie konwertujemy na Double
                    float totalZarobekFloat = rs.getFloat("total_zarobek");
                    return (double) totalZarobekFloat;
                }
            }
        }
        return 0.0;
    }

    // Metoda do przeliczania i aktualizowania zysku uprawy
    public void recalculateUprawaProfit(int uprawaId) throws SQLException {
        double totalKoszt = getTotalKosztForUprawa(uprawaId);
        double totalZarobek = getTotalZarobekForUprawa(uprawaId);
        double nowyZysk = totalZarobek - totalKoszt;

        String sqlUpdate = "UPDATE uprawy SET zysk = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            pstmt.setDouble(1, nowyZysk);
            pstmt.setInt(2, uprawaId);
            pstmt.executeUpdate();
        }
    }

    // Metoda do aktualizacji daty zbioru dla uprawy
    public void updateUprawaDataZbioru(int uprawaId, LocalDate dataZbioru) throws SQLException {
        String sql = "UPDATE uprawy SET data_zbioru = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (dataZbioru != null) {
                pstmt.setDate(1, java.sql.Date.valueOf(dataZbioru));
            } else {
                pstmt.setNull(1, Types.DATE);
            }
            pstmt.setInt(2, uprawaId);
            pstmt.executeUpdate();
        }
    }
}