package com.example.projekt_po1.objects;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Field extends BaseEnity {
    private User user;
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty area = new SimpleDoubleProperty();
    private final StringProperty localisation = new SimpleStringProperty();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getArea() {
        return area.get();
    }

    public DoubleProperty areaProperty() {
        return area;
    }

    public void setArea(double area) {
        this.area.set(area);
    }

    public String getLocalisation() {
        return localisation.get();
    }

    public StringProperty localisationProperty() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation.set(localisation);
    }
}