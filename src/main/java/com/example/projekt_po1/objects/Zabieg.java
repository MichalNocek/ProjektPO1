package com.example.projekt_po1.objects;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Zabieg extends BaseEnity {
    private final StringProperty nazwa = new SimpleStringProperty();
    private final StringProperty typ = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> data = new SimpleObjectProperty<>();
    private final StringProperty dawka = new SimpleStringProperty();
    private final DoubleProperty koszt = new SimpleDoubleProperty();
    private final StringProperty rodzajZabiegu = new SimpleStringProperty();
    private final DoubleProperty zarobek = new SimpleDoubleProperty();
    private final int uprawaId;

    // Konstruktor ze wszystkimi polami z bazy
    public Zabieg(int id, String nazwa, String typ, LocalDate data, String dawka, double koszt, String rodzajZabiegu, double zarobek, int uprawaId) {
        this.id = id;
        this.nazwa.set(nazwa);
        this.typ.set(typ);
        this.data.set(data);
        this.dawka.set(dawka);
        this.koszt.set(koszt);
        this.rodzajZabiegu.set(rodzajZabiegu);
        this.zarobek.set(zarobek);
        this.uprawaId = uprawaId;
    }

    // Konstruktor do tworzenia nowego zabiegu (bez id)
    public Zabieg(String nazwa, String typ, LocalDate data, String dawka, double koszt, String rodzajZabiegu, double zarobek, int uprawaId) {
        this(0, nazwa, typ, data, dawka, koszt, rodzajZabiegu, zarobek, uprawaId);
    }

    // Pusty konstruktor dla FXML PropertyValueFactory
    public Zabieg() {
        this.uprawaId = 0;
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

    public String getTyp() {
        return typ.get();
    }
    public StringProperty typProperty() {
        return typ;
    }
    public void setTyp(String typ) {
        this.typ.set(typ);
    }

    public LocalDate getData() {
        return data.get();
    }
    public ObjectProperty<LocalDate> dataProperty() {
        return data;
    }
    public void setData(LocalDate data) {
        this.data.set(data);
    }

    public String getDawka() {
        return dawka.get();
    }
    public StringProperty dawkaProperty() {
        return dawka;
    }
    public void setDawka(String dawka) {
        this.dawka.set(dawka);
    }

    public double getKoszt() {
        return koszt.get();
    }
    public DoubleProperty kosztProperty() {
        return koszt;
    }
    public void setKoszt(double koszt) {
        this.koszt.set(koszt);
    }

    public String getRodzajZabiegu() {
        return rodzajZabiegu.get();
    }
    public StringProperty rodzajZabieguProperty() {
        return rodzajZabiegu;
    }
    public void setRodzajZabiegu(String rodzajZabiegu) {
        this.rodzajZabiegu.set(rodzajZabiegu);
    }

    public double getZarobek() {
        return zarobek.get();
    }
    public DoubleProperty zarobekProperty() {
        return zarobek;
    }
    public void setZarobek(double zarobek) {
        this.zarobek.set(zarobek);
    }

    public int getUprawaId() {
        return uprawaId;
    }
}