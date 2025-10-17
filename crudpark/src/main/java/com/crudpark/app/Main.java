package com.crudpark.app;

import com.crudpark.view.LoginView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Ejecutar Swing en el hilo correcto (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}