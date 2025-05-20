package jdbc;

import com.example.projekt_po1.objects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrudOperation {
    User user = new User();
        //Rejestracja użytkownika
    public int registerUser(String logintest, String nazwiskotest) throws SQLException {
        String sql = "Select * from uzytkownicy where login =? and password =? ";
        String add = "INSERT INTO uzytkownicy (login, password) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,logintest);
            pstmt.setString(2,nazwiskotest);
            ResultSet rs = pstmt.executeQuery();
            if(!rs.next()){

                try (Connection conn2 = DBConnection.getConnection();
                     PreparedStatement pstmt2 = conn2.prepareStatement(add)) {

                    pstmt2.setString(1, logintest);
                    pstmt2.setString(2, nazwiskotest);
                    pstmt2.executeUpdate();
                    return 1;
                }
            }
            return 0;
    }
}
    //logowanie do aplikacji
    public int loginUser(String login,String haslo) throws SQLException {
        String sql = "Select id,login , password from uzytkownicy where login = ? and password = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,login);
            pstmt.setString(2,haslo);
                try(ResultSet rs = pstmt.executeQuery()){
                    if(rs.next()){
                        String iqueue = rs.getString("id");
                        user.setId(Integer.parseInt(iqueue));
                        return Integer.parseInt(iqueue);
                    }else{
                        return 0;
                    }

                }


        }


    }

    public int addfield(String name, double area, String localisation) throws SQLException {

        //sprawdzenie czy pole już istnieje
        String sql2 = "Select * from pola where nazwa =? AND uzytkownik_id =?";
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql2)){
            pstmt.setString(1,name);
            pstmt.setInt(2,user.getId());
            ResultSet rs = pstmt.executeQuery();
            if(!rs.next()){
                //dodanie pola
                String sql = "INSERT INTO pola (uzytkownik_id,nazwa, powierzchnia, lokalizacja) VALUES ( ?,?, ?, ?)";
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                    pstmt2.setInt(1, user.getId());
                    pstmt2.setString(2, name);
                    pstmt2.setDouble(3, area);
                    pstmt2.setString(4, localisation);
                    pstmt2.executeUpdate();
                    System.out.print("Dodano pole");
                    return 1;
                }

            }
            return 0;
        }







    }


}