package com.crudpark.view;

import com.crudpark.controller.SalidaController;
import com.crudpark.model.Operador;
import com.crudpark.model.Ticket;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class SalidaView extends JFrame {

    private JTextField placaField;
    private JLabel infoTicketLabel;
    private JLabel montoLabel;
    private JButton buscarButton;
    private JButton pagarButton;
    private Operador operador;
    private SalidaController controller;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SalidaView(Operador operador) {
        this.operador = operador;
        this.controller = new SalidaController(this);
        setTitle("Registro de Salida y Cobro | Operador: " + operador.getUsuario());
        setSize(450, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        placaField = new JTextField(15);
        buscarButton = new JButton("Buscar Ticket");
        searchPanel.add(new JLabel("Placa o Folio/QR:")); // Etiqueta mejorada
        searchPanel.add(placaField);
        searchPanel.add(buscarButton);
        add(searchPanel, BorderLayout.NORTH);

        // Panel de información y cobro
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoTicketLabel = new JLabel("<html>Esperando búsqueda...</html>", SwingConstants.CENTER); // Usar HTML para formato
        montoLabel = new JLabel("Monto a Pagar: $0.00", SwingConstants.CENTER);
        montoLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Fuente más grande
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

        // Inicializar el estado de la vista
        resetView();
    }

    /**
     * Muestra la información del ticket en los campos de la vista.
     */
    public void displayTicketInfo(Ticket ticket, BigDecimal monto) {
        if (ticket != null) {
            String info = String.format("<html>Folio: #%d<br>Placa: %s<br>Ingreso: %s</html>",
                    ticket.getId(),
                    ticket.getPlaca(),
                    ticket.getFechaIngreso().format(FORMATTER));

            infoTicketLabel.setText(info);
            // Mostrar monto con formato de 2 decimales
            montoLabel.setText(String.format("MONTO: $%.2f", monto));
            pagarButton.setEnabled(monto.compareTo(new BigDecimal("-1.00")) != 0); // Deshabilita si es error de tarifa
        } else {
            resetView();
            infoTicketLabel.setText("<html>Ticket no encontrado, ya cerrado o placa incorrecta.</html>");
        }
    }

    /**
     * Reinicia los campos a su estado inicial después de un cobro o al iniciar.
     */
    public void resetView() {
        placaField.setText("");
        infoTicketLabel.setText("<html>Esperando búsqueda...</html>");
        montoLabel.setText("Monto a Pagar: $0.00");
        pagarButton.setEnabled(false);
    }

    public void showMessage(String message, boolean isError) {
        JOptionPane.showMessageDialog(this, message, isError ? "Error" : "Transacción Exitosa", isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }

    // Getters necesarios para el Controller
    public Ticket getTicketActual() { return controller.getTicketActual(); }
    public BigDecimal getMontoActual() { return controller.getMontoActual(); }
}