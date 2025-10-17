package com.crudpark.controller;

import com.crudpark.model.Operador;
import com.crudpark.service.LoginService;
import com.crudpark.view.LoginView;
import com.crudpark.view.MainMenuView;

public class LoginController {

    private LoginService loginService;
    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        this.loginService = new LoginService();
    }

    public void handleLogin(String usuario, String password) {
        Operador operador = loginService.attemptLogin(usuario, password);

        if (operador != null) {
            view.showSuccess("Bienvenido, " + operador.getNombre() + "!");

            MainMenuView mainView = new MainMenuView(operador);
            mainView.setVisible(true);

            view.dispose(); // Cierra el Login
        } else {
            view.showError("Credenciales inválidas o el operador no está activo.");
        }
    }
}