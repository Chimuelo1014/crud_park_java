package com.crudpark.model;

import java.time.LocalDateTime;

public class Ticket {
    private int id; // Folio o ID del ticket
    private String placa;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaSalida; // NULL si está dentro
    private Operador operadorIngreso;
    private Operador operadorSalida;
    private String tipoVehiculo; // <-- Este es el campo

    // Constructor para un NUEVO ingreso
    public Ticket(String placa, LocalDateTime fechaIngreso, Operador operadorIngreso) {
        this.placa = placa;
        this.fechaIngreso = fechaIngreso;
        this.operadorIngreso = operadorIngreso;
        this.tipoVehiculo = "Automovil"; // Valor por defecto
    }

    // Constructor completo (para obtener datos de la DB)
    public Ticket(int id, String placa, LocalDateTime fechaIngreso, LocalDateTime fechaSalida,
                  Operador operadorIngreso, Operador operadorSalida, String tipoVehiculo) {
        this.id = id;
        this.placa = placa;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.operadorIngreso = operadorIngreso;
        this.operadorSalida = operadorSalida;
        this.tipoVehiculo = tipoVehiculo;
    }

    // Getters
    public int getId() { return id; }
    public String getPlaca() { return placa; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public Operador getOperadorIngreso() { return operadorIngreso; }
    public boolean estaAbierto() { return fechaSalida == null; }
    public String getTipoVehiculo() { return tipoVehiculo; } // <-- Método corregido
    // ... otros getters y setters si son necesarios
}