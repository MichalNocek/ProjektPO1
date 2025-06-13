package com.example.projekt_po1.objects;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Uprawa extends BaseEnity {

    private final StringProperty nazwa = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dataSiewu = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> dataZbioru = new SimpleObjectProperty<>();
    private final DoubleProperty zysk = new SimpleDoubleProperty();
    private final IntegerProperty poleId = new SimpleIntegerProperty();


    public Uprawa(int id, String nazwa, LocalDate dataSiewu, LocalDate dataZbioru, Double zysk, int poleId) {
        this.id = id; // Odziedziczone z BaseEnity
        this.nazwa.set(nazwa);
        this.dataSiewu.set(dataSiewu);
        this.dataZbioru.set(dataZbioru);

        if (zysk != null) {
            this.zysk.set(zysk);
        } else {
            this.zysk.set(0.0);
        }
        this.poleId.set(poleId);
    }

    public Uprawa() {

    }



    public String getNazwa() {
        return nazwa.get();
    }

    public StringProperty nazwaProperty() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa.set(nazwa);
    }

    public LocalDate getDataSiewu() {
        return dataSiewu.get();
    }

    public ObjectProperty<LocalDate> dataSiewuProperty() {
        return dataSiewu;
    }

    public void setDataSiewu(LocalDate dataSiewu) {
        this.dataSiewu.set(dataSiewu);
    }

    public LocalDate getDataZbioru() {
        return dataZbioru.get();
    }

    public ObjectProperty<LocalDate> dataZbioruProperty() {
        return dataZbioru;
    }

    public void setDataZbioru(LocalDate dataZbioru) {
        this.dataZbioru.set(dataZbioru);
    }


    public double getZysk() {
        return zysk.get();
    }


    public DoubleProperty zyskProperty() {
        return zysk;
    }

    // Setter dla zysku (przyjmuje prymitywny double)
    public void setZysk(double zysk) {
        this.zysk.set(zysk);
    }

    // Dodatkowy setter dla Double (jeśli zysk może być null z bazy)
    public void setZysk(Double zysk) {
        if (zysk != null) {
            this.zysk.set(zysk);
        } else {
            this.zysk.set(0.0); // Ustaw 0.0, jeśli zysk jest null
        }
    }


    public int getPoleId() {
        return poleId.get();
    }

    public IntegerProperty poleIdProperty() {
        return poleId;
    }

    public void setPoleId(int poleId) {
        this.poleId.set(poleId);
    }
}