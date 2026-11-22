package com.relief_system;

import com.model.ReliefSystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {
    private static final int MAX_TRIES = 3;
    private static int failedTries;

    @FXML
    private VBox errorBox;
    @FXML
    private Label errorDetail;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label passwordError;
    @FXML
    private Button resetButton;

    @FXML
    private void initialize() {
        usernameField.textProperty().addListener((obs, old, now) -> showError(false));
        passwordField.textProperty().addListener((obs, old, now) -> showError(false));
    }

    @FXML
    private void logIn() {
        if (ReliefSystem.getInstance().signIn(usernameField.getText(), passwordField.getText())) {
            failedTries = 0;
            App.setRoot("home");
            return;
        }
        failedTries++;
        int left = Math.max(0, MAX_TRIES - failedTries);
        errorDetail.setText(left == 0 ? "Too many tries. Reset your password."
                : "Check it and try again, or reset it below. " + left + (left == 1 ? " attempt left." : " attempts left."));
        showError(true);
    }

    private void showError(boolean error) {
        Ui.show(errorBox, error);
        Ui.show(passwordError, error);
        Ui.show(resetButton, error);
        Ui.error(passwordField, error);
    }

    @FXML
    private void back() {
        App.setRoot("welcome");
    }

    @FXML
    private void forgotPassword() {
        App.setRoot("reset-password");
    }

    @FXML
    private void createAccount() {
        App.setRoot("signup");
    }

    @FXML
    private void volunteer() {
        // TODO: volunteer log in
    }
}
