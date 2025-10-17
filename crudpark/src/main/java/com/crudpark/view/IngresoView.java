package com.crudpark.view;

import com.crudpark.controller.IngresoController;
import com.crudpark.model.Operador;
import javax.swing.*;
import java.awt.*;

public class IngresoView extends JFrame {

    private JTextField placaField;
    private Operador operador;

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

    /**
     * Muestra mensajes de error o éxito al usuario.
     * Se elimina la limpieza automática del campo para que el Controller decida cuándo limpiar.
     * @param message El mensaje a mostrar.
     * @param isError Si es un mensaje de error (true) o informativo (false).
     */
    public void showMessage(String message, boolean isError) {
        int type = isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE;
        // El controller ahora maneja la limpieza del campo.
        JOptionPane.showMessageDialog(this, message, isError ? "Error de Ingreso" : "Ingreso Exitoso", type);
    }

    /**
     * Nuevo método público llamado por el Controller para limpiar el campo.
     * Esto asegura que solo se limpie después de que todo el proceso (DB + Impresión) finalice con éxito.
     */
    public void clearPlaca() {
        placaField.setText("");
    }
}