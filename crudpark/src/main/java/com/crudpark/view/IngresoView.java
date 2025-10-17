package com.crudpark.view;

import com.crudpark.controller.IngresoController;
import com.crudpark.model.Operador;
import javax.swing.*;
import java.awt.*;

public class IngresoView extends JFrame {

    private JTextField placaField;
    private Operador operador; // Necesitamos saber quién está operando

    public IngresoView(Operador operador) {
        this.operador = operador;
        setTitle("Registro de Ingreso | Operador: " + operador.getUsuario());
        setSize(400, 150);
        setLocationRelativeTo(null);

        IngresoController controller = new IngresoController(this);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        placaField = new JTextField(10);
        JButton registrarButton = new JButton("Registrar Ingreso");

        panel.add(new JLabel("Placa del Vehículo:", SwingConstants.RIGHT));
        panel.add(placaField);
        panel.add(new JLabel(""));
        panel.add(registrarButton);

        add(panel, BorderLayout.CENTER);

        registrarButton.addActionListener(e -> {
            String placa = placaField.getText().trim().toUpperCase();
            controller.handleRegistro(placa, operador);
        });
    }

    public void showMessage(String message, boolean isError) {
        int type = isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(this, message, isError ? "Error de Ingreso" : "Ingreso Exitoso", type);
        if (!isError) {
            placaField.setText(""); // Limpiar el campo después de un ingreso exitoso
        }
    }
}