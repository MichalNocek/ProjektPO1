package jdbc;

import jdbc.DBConnection;

import java.sql.*;

public class PersonCRUD {


//    // Create
//    public void addPerson(String firstName, String lastName, int age) {
//        String sql = "INSERT INTO users (firstName, lastName, age) VALUES (?, ?, ?)";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, firstName);
//            pstmt.setString(2, lastName);
//            pstmt.setInt(3, age);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Read
//    public void viewAllPerson() {
//        String sql = "Select * from users";
//        try (Connection conn = DBConnection.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                System.out.println( ": " +
//                        rs.getString("firstName") + " " +
//                        rs.getString("lastName") + ", " +
//                        rs.getInt("age"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Update
//    public void updatePerson(int id, String firstName, String lastName, int
//            age) {
//        String sql = "";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, firstName);
//            pstmt.setString(2, lastName);
//            pstmt.setInt(3, age);
//            pstmt.setInt(4, id);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Delete
//    public void deletePerson(int id) {
//        String sql = "";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, id);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
