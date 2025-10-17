package com.crudpark.view;

import com.crudpark.controller.LoginController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginView extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private LoginController controller;

    public LoginView() {
        setTitle("CrudPark - Login Operador");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.controller = new LoginController(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10)); // Usamos GridLayout para centrar

        userField = new JTextField(15);
        passField = new JPasswordField(15);
        JButton loginButton = new JButton("Iniciar Sesión");

        panel.add(new JLabel("Usuario:"));
        panel.add(userField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(passField);
        panel.add(new JLabel("")); // Celda vacía para espacio
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> {
            String usuario = userField.getText();
            String password = new String(passField.getPassword());
            controller.handleLogin(usuario, password);
        });
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error de Login", JOptionPane.ERROR_MESSAGE);
    }
}