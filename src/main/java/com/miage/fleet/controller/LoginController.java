package com.miage.fleet.controller;

import com.miage.fleet.FleetApp;
import com.miage.fleet.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private HBox errorBox;
    @FXML private Label errorLabel;
    @FXML private Button togglePwdBtn;

    private boolean passwordVisible = false;
    private TextField visiblePasswordField;

    @FXML
    public void initialize() {
        // Allow Enter key to trigger login
        emailField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        loginBtn.setDisable(true);
        loginBtn.setText("Connexion en cours...");

        boolean ok = DatabaseService.getInstance().login(email, password);

        if (ok) {
            try {
                FleetApp.showMain();
            } catch (IOException ex) {
                showError("Erreur lors du chargement de l'application.");
                loginBtn.setDisable(false);
                loginBtn.setText("Se connecter");
            }
        } else {
            showError("Identifiants incorrects. Vérifiez votre email et mot de passe.");
            loginBtn.setDisable(false);
            loginBtn.setText("Se connecter");
        }
    }

    @FXML
    private void togglePasswordVisibility() {
        // Simple feedback - in full impl would swap PasswordField <-> TextField
        passwordVisible = !passwordVisible;
        togglePwdBtn.setText(passwordVisible ? "🙈" : "👁");
    }

    @FXML
    private void fillAdminCredentials() {
        emailField.setText("i.kone@societe.ci");
        passwordField.setText("admin123");
        hideError();
    }

    @FXML
    private void fillAgentCredentials() {
        emailField.setText("a.coulibaly@societe.ci");
        passwordField.setText("agent123");
        hideError();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorBox.setVisible(true);
        errorBox.setManaged(true);
    }

    private void hideError() {
        errorBox.setVisible(false);
        errorBox.setManaged(false);
    }
}
