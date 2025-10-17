package com.crudpark.service;

import com.crudpark.dao.OperadorDAO;
import com.crudpark.model.Operador;

public class LoginService {

    private OperadorDAO operadorDAO;

    public LoginService() {
        this.operadorDAO = new OperadorDAO();
    }

    public Operador attemptLogin(String usuario, String password) {
        Operador operador = operadorDAO.findByUsername(usuario);

        if (operador == null) {
            return null; // Usuario no existe
        }

        // SIMULACIÓN DE VERIFICACIÓN DE CONTRASEÑA (Debe ser reemplazado por un hash seguro)
        if (!password.equals(operador.getPasswordHash())) {
            return null; // Contraseña incorrecta
        }

        if (!operador.isActivo()) {
            return null; // Operador inactivo
        }

        // Éxito
        return new Operador(operador.getId(), operador.getUsuario(), operador.getNombre(), operador.isActivo());
    }
}