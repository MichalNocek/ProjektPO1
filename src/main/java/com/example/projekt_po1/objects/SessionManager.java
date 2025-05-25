package com.example.projekt_po1.objects;

public class SessionManager {
    private static int loggedInUserId = 0; // Przechowuje ID zalogowanego użytkownika

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static void setLoggedInUserId(int userId) {
        loggedInUserId = userId;
    }

    public static void logout() {
        loggedInUserId = 0;
    }
}