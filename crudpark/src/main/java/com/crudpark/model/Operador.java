package com.crudpark.model;

public class Operador {
    private int id;
    private String usuario;
    private String nombre;
    private boolean activo;
    private String passwordHash;

    // Constructor completo (para uso del DAO)
    public Operador(int id, String usuario, String nombre, boolean activo, String passwordHash) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.activo = activo;
        this.passwordHash = passwordHash;
    }

    // Constructor sin hash (para usar después del login exitoso)
    public Operador(int id, String usuario, String nombre, boolean activo) {
        this(id, usuario, nombre, activo, null);
    }

    // Getters
    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public boolean isActivo() { return activo; }
    public String getPasswordHash() { return passwordHash; }
}