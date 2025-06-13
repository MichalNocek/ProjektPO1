package jdbc;

import com.example.projekt_po1.objects.Field;
import com.example.projekt_po1.objects.SessionManager;
import com.example.projekt_po1.objects.Uprawa;
import com.example.projekt_po1.objects.Zabieg;
import com.example.projekt_po1.objects.User; // Dodano import User

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Dodano import Statement
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CrudOperation {

    public int registerUser(String login, String password) throws SQLException {
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

            try (PreparedStatement pstmtAdd = conn.prepareStatement(add, Statement.RETURN_GENERATED_KEYS)) {
                pstmtAdd.setString(1, login);
                pstmtAdd.setString(2, password);
                pstmtAdd.executeUpdate();
                ResultSet rs = pstmtAdd.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        }
    }

    public User loginUser(String login, String password) throws SQLException {
        String sql = "SELECT id, login, password FROM uzytkownicy WHERE login = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String userLogin = rs.getString("login");
                    String userPassword = rs.getString("password");
                    return new User(id, userLogin, userPassword);
                }
            }
        }
        return null;
    }

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

    public void deleteField(int fieldId) throws SQLException {
        int userId = SessionManager.getLoggedInUserId();
        if (userId == 0) {
            throw new IllegalStateException("Użytkownik nie jest zalogowany, nie można usunąć pola.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlDeleteZabiegi = "DELETE FROM zabiegi WHERE uprawa_id IN (SELECT id FROM uprawy WHERE pole_id = ?)";
            try (PreparedStatement pstmtDeleteZabiegi = conn.prepareStatement(sqlDeleteZabiegi)) {
                pstmtDeleteZabiegi.setInt(1, fieldId);
                pstmtDeleteZabiegi.executeUpdate();
            }

            String sqlDeleteUprawy = "DELETE FROM uprawy WHERE pole_id = ?";
            try (PreparedStatement pstmtDeleteUprawy = conn.prepareStatement(sqlDeleteUprawy)) {
                pstmtDeleteUprawy.setInt(1, fieldId);
                pstmtDeleteUprawy.executeUpdate();
            }

            String sqlDeleteField = "DELETE FROM pola WHERE id = ? AND uzytkownik_id = ?";
            try (PreparedStatement pstmtDeleteField = conn.prepareStatement(sqlDeleteField)) {
                pstmtDeleteField.setInt(1, fieldId);
                pstmtDeleteField.setInt(2, userId);
                pstmtDeleteField.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

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

            if (zysk != null) {
                pstmt.setDouble(5, zysk);
            } else {
                pstmt.setNull(5, Types.REAL);
            }

            pstmt.executeUpdate();
            System.out.println("Dodano uprawę dla pola ID: " + poleId);
        }
    }

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

                    float retrievedZyskFloat = rs.getFloat("zysk");
                    if (rs.wasNull()) {
                        uprawa.setZysk(0.0);
                    } else {
                        uprawa.setZysk((double) retrievedZyskFloat);
                    }
                    uprawaList.add(uprawa);
                }
            }
        }
        return uprawaList;
    }

    public void deleteUprawa(int uprawaId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlDeleteZabiegi = "DELETE FROM zabiegi WHERE uprawa_id = ?";
            try (PreparedStatement pstmtDeleteZabiegi = conn.prepareStatement(sqlDeleteZabiegi)) {
                pstmtDeleteZabiegi.setInt(1, uprawaId);
                pstmtDeleteZabiegi.executeUpdate();
            }

            String sqlDeleteUprawa = "DELETE FROM uprawy WHERE id = ?";
            try (PreparedStatement pstmtDeleteUprawa = conn.prepareStatement(sqlDeleteUprawa)) {
                pstmtDeleteUprawa.setInt(1, uprawaId);
                pstmtDeleteUprawa.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void addZabieg(int uprawaId, String nazwa, String typ, LocalDate data, String dawka, Double koszt, String rodzajZabiegu, Double zarobek) throws SQLException {
        String sql = "INSERT INTO zabiegi (uprawa_id, nazwa, typ, data, dawka, koszt, rodzaj_zabiegu, zarobek) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, uprawaId);

            if (nazwa != null && !nazwa.isEmpty()) {
                pstmt.setString(2, nazwa);
            } else {
                pstmt.setNull(2, Types.VARCHAR);
            }

            if (typ != null && !typ.isEmpty()) {
                pstmt.setString(3, typ);
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }

            if (data != null) {
                pstmt.setDate(4, java.sql.Date.valueOf(data));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            if (dawka != null && !dawka.isEmpty()) {
                pstmt.setString(5, dawka);
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }

            if (koszt != null) {
                pstmt.setDouble(6, koszt);
            } else {
                pstmt.setNull(6, Types.REAL);
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
            recalculateUprawaProfit(uprawaId);
        }
    }

    public List<Zabieg> getZabiegiForUprawa(int uprawaId) throws SQLException {
        List<Zabieg> zabiegList = new ArrayList<>();
        String sql = "SELECT id, nazwa, typ, data, dawka, koszt, rodzaj_zabiegu, zarobek FROM zabiegi WHERE uprawa_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, uprawaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    float retrievedKosztFloat = rs.getFloat("koszt");
                    Double finalKoszt = rs.wasNull() ? 0.0 : (double) retrievedKosztFloat;

                    double retrievedZarobek = rs.getDouble("zarobek");
                    boolean zarobekWasNull = rs.wasNull();

                    Zabieg zabieg = new Zabieg(
                            rs.getInt("id"),
                            rs.getString("nazwa"),
                            rs.getString("typ"),
                            rs.getDate("data") != null ? rs.getDate("data").toLocalDate() : null,
                            rs.getString("dawka"),
                            finalKoszt,
                            rs.getString("rodzaj_zabiegu"),
                            zarobekWasNull ? 0.0 : retrievedZarobek,
                            uprawaId
                    );
                    zabiegList.add(zabieg);
                }
            }
        }
        return zabiegList;
    }

    public void deleteZabieg(int zabiegId, int uprawaId) throws SQLException {
        String sql = "DELETE FROM zabiegi WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, zabiegId);
            pstmt.executeUpdate();
            recalculateUprawaProfit(uprawaId);
        }
    }

    public double getTotalKosztForUprawa(int uprawaId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(koszt), 0.0) AS total_koszt FROM zabiegi WHERE uprawa_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, uprawaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    float totalKosztFloat = rs.getFloat("total_koszt");
                    return (double) totalKosztFloat;
                }
            }
        }
        return 0.0;
    }

    public double getTotalZarobekForUprawa(int uprawaId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(zarobek), 0.0) AS total_zarobek FROM zabiegi WHERE uprawa_id = ? AND UPPER(rodzaj_zabiegu) = 'ZBIÓR'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, uprawaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    float totalZarobekFloat = rs.getFloat("total_zarobek");
                    return (double) totalZarobekFloat;
                }
            }
        }
        return 0.0;
    }

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