package com.crudpark.view;

import com.crudpark.controller.SalidaController;
import com.crudpark.model.Operador;
import com.crudpark.model.Ticket;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class SalidaView extends JFrame {

    private JTextField placaField;
    private JLabel infoTicketLabel;
    private JLabel montoLabel;
    private JButton buscarButton;
    private JButton pagarButton;
    private Operador operador;
    private SalidaController controller;

    public SalidaView(Operador operador) {
        this.operador = operador;
        this.controller = new SalidaController(this);
        setTitle("Registro de Salida y Cobro | Operador: " + operador.getUsuario());
        setSize(450, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        placaField = new JTextField(10);
        buscarButton = new JButton("Buscar Ticket");
        searchPanel.add(new JLabel("Placa:"));
        searchPanel.add(placaField);
        searchPanel.add(buscarButton);
        add(searchPanel, BorderLayout.NORTH);

        // Panel de información y cobro
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoTicketLabel = new JLabel("Esperando búsqueda...");
        montoLabel = new JLabel("Monto a Pagar: $0.00", SwingConstants.CENTER);
        montoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        pagarButton = new JButton("PAGAR Y CERRAR SALIDA");
        pagarButton.setEnabled(false);

        infoPanel.add(infoTicketLabel);
        infoPanel.add(montoLabel);
        infoPanel.add(pagarButton);
        add(infoPanel, BorderLayout.CENTER);

        // Listeners
        buscarButton.addActionListener(e -> {
            controller.handleBuscar(placaField.getText().trim().toUpperCase());
        });

        pagarButton.addActionListener(e -> {
            controller.handlePagar(operador);
        });
    }

    public void displayTicketInfo(Ticket ticket, BigDecimal monto) {
        if (ticket != null) {
            infoTicketLabel.setText("<html>Ticket #" + ticket.getId() + "<br>Ingreso: " + ticket.getFechaIngreso() + "</html>");
            montoLabel.setText("MONTO: $" + monto.toString());
            pagarButton.setEnabled(monto.compareTo(BigDecimal.ZERO) >= 0);
        } else {
            infoTicketLabel.setText("Ticket no encontrado o ya cerrado.");
            montoLabel.setText("Monto a Pagar: $0.00");
            pagarButton.setEnabled(false);
        }
    }

    public void showMessage(String message, boolean isError) {
        JOptionPane.showMessageDialog(this, message, isError ? "Error" : "Transacción Exitosa", isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }
}