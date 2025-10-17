package com.crudpark.controller;

import com.crudpark.model.Operador;
import com.crudpark.service.ReporteService;
import javax.swing.JOptionPane;
import java.math.BigDecimal;

public class ReporteController {

    private ReporteService reporteService;
    private Operador operador;

    public ReporteController(Operador operador) {
        this.operador = operador;
        this.reporteService = new ReporteService();
    }

    public void showReporte() {
        // Validación básica: Solo operadores 'admin' pueden ver reportes (Ejemplo de Regla de Negocio)
        if (!operador.getUsuario().equals("operador1")) {
            JOptionPane.showMessageDialog(null, "Permiso denegado. Solo administradores pueden ver reportes.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal ganancias = reporteService.getGananciasDeHoy();

        String mensaje = String.format(
                "<html><h2>Ganancias del Día</h2><p>Fecha: %s</p><p>Total Recaudado: <b>$%,.2f</b></p></html>",
                java.time.LocalDate.now().toString(),
                ganancias
        );

        JOptionPane.showMessageDialog(null, mensaje, "Reporte Diario", JOptionPane.INFORMATION_MESSAGE);
    }
}