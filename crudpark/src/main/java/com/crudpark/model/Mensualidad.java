package com.crudpark.model;

import java.time.LocalDate;

public class Mensualidad {
    private int id;
    private String placa;
    private String nombreCliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa;

    public Mensualidad(int id, String placa, String nombreCliente, LocalDate fechaInicio, LocalDate fechaFin, boolean activa) {
        this.id = id;
        this.placa = placa;
        this.nombreCliente = nombreCliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = activa;
    }

    // Getters
    public String getPlaca() { return placa; }
    public LocalDate getFechaFin() { return fechaFin; }
    public boolean isActiva() { return activa; }

    // Lógica de Negocio: Verifica si la mensualidad es VÁLIDA HOY
    public boolean esMensualidadValidaHoy() {
        LocalDate hoy = LocalDate.now();
        return this.activa && (hoy.isEqual(fechaInicio) || hoy.isAfter(fechaInicio)) && (hoy.isEqual(fechaFin) || hoy.isBefore(fechaFin));
    }

    // ...
}