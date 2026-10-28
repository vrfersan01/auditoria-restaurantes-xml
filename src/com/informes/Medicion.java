package com.informes;

public class Medicion {

    private final String metodo;
    private final double milisegundos;
    private final double megabytes;

    public Medicion(String metodo, double milisegundos, double megabytes) {
        this.metodo = metodo;
        this.milisegundos = milisegundos;
        this.megabytes = megabytes;
    }

    public String getMetodo() {
        return metodo;
    }

    public double getMilisegundos() {
        return milisegundos;
    }

    public double getMegabytes() {
        return megabytes;
    }
}