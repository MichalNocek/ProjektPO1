package com.example.projekt_po1.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User extends BaseEnity {
    private final StringProperty login = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();

    public User() {
        super();
    }

    public User(int id, String login, String password) {
        super();
        this.setId(id);
        this.login.set(login);
        this.password.set(password);
    }

    public String getLogin() {
        return login.get();
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty loginProperty() {
        return login;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public void setPassword(String password) {
        this.password.set(password);
    }
}