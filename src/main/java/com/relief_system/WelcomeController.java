package com.relief_system;

import javafx.fxml.FXML;

public class WelcomeController {
    @FXML
    private void createAccount() {
        App.setRoot("signup");
    }

    @FXML
    private void logIn() {
        App.setRoot("login");
    }

    @FXML
    private void guestHelp() {
        App.setRoot("guest-help");
    }
}
