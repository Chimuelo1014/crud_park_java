package com.crudpark.view;

import com.crudpark.model.Operador;
import javax.swing.*;
import java.awt.*;
import com.crudpark.controller.ReporteController;



public class MainMenuView extends JFrame {

    private Operador operadorLogueado;

    public MainMenuView(Operador operador) {
        this.operadorLogueado = operador;
        setTitle("CrudPark - Sistema Operativo | Operador: " + operador.getNombre());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); // Usamos BorderLayout

        // Panel de Botones (Menú Central)
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        // Botón 1: Ingreso de Vehículo
        JButton btnIngreso = new JButton("1. Ingreso de Vehículo");
        btnIngreso.setPreferredSize(new Dimension(200, 80));
        btnIngreso.addActionListener(e -> {
            // Abrir la vista de Ingreso
            new IngresoView(operadorLogueado).setVisible(true);
        });

        // Botón 2: Salida de Vehículo
        JButton btnSalida = new JButton("2. Salida de Vehículo");
        btnSalida.setPreferredSize(new Dimension(200, 80));
        btnSalida.addActionListener(e -> {
            // Abrir la vista de Salida
            new SalidaView(operadorLogueado).setVisible(true);
        });

        // Botón 3: Reporte de Ganancias
        JButton btnReporte = new JButton("3. Reporte Diario");
        btnReporte.setPreferredSize(new Dimension(200, 80));
        btnReporte.addActionListener(e -> {
            // Llama al controlador para obtener y mostrar el reporte
            ReporteController controller = new ReporteController(operadorLogueado);
            controller.showReporte();
        });


        menuPanel.add(btnIngreso);
        menuPanel.add(btnSalida);
        menuPanel.add(btnReporte);

        add(menuPanel, BorderLayout.CENTER);
    }
}