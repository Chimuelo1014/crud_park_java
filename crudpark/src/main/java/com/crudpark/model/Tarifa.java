package com.crudpark.model;

import java.math.BigDecimal;

public class Tarifa {
    private int id;
    private String nombre;
    private BigDecimal valorHoraBase; // Usamos BigDecimal para cálculos monetarios precisos
    private BigDecimal valorFraccionMinuto;
    private int minutosGracia;
    private boolean activo;

    public Tarifa(int id, String nombre, BigDecimal valorHoraBase, BigDecimal valorFraccionMinuto, int minutosGracia, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.valorHoraBase = valorHoraBase;
        this.valorFraccionMinuto = valorFraccionMinuto;
        this.minutosGracia = minutosGracia;
        this.activo = activo;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getValorHoraBase() { return valorHoraBase; }
    public BigDecimal getValorFraccionMinuto() { return valorFraccionMinuto; }
    public int getMinutosGracia() { return minutosGracia; }
    public boolean isActivo() { return activo; }
}