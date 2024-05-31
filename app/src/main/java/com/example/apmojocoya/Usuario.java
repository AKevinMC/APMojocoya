package com.example.apmojocoya;

public class Usuario {
    private String name;
    private int medidoresCount;

    public Usuario(String name, int medidoresCount) {
        this.name = name;
        this.medidoresCount = medidoresCount;
    }

    public String getName() {
        return name;
    }

    public int getMedidoresCount() {
        return medidoresCount;
    }
}
