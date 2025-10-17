package com.crudpark.controller;

import com.crudpark.model.Operador;
import com.crudpark.service.ParkingService;
import com.crudpark.view.IngresoView;

public class IngresoController {

    private ParkingService parkingService;
    private IngresoView view;

    public IngresoController(IngresoView view) {
        this.view = view;
        this.parkingService = new ParkingService();
    }

    public void handleRegistro(String placa, Operador operador) {
        if (placa.isEmpty()) {
            view.showMessage("La placa no puede estar vacía.", true);
            return;
        }

        String resultado = parkingService.registrarIngreso(placa, operador);

        if (resultado.startsWith("ERROR")) {
            view.showMessage(resultado, true);
        } else {
            view.showMessage(resultado, false);
        }
    }
}