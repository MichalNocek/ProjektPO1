package com.example.projekt_po1.objects;

public abstract class BaseEnity {
    protected int id; // Zmienna instancji, nie statyczna
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id; // <-- POPRAWKA! Odwołujemy się do instancji
    }
}