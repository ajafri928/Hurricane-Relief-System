package com.relief_system;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ResetPasswordController {
    @FXML
    private TextField identifierField;
    @FXML
    private VBox codeBox;
    @FXML
    private TextField codeField;
    @FXML
    private Label errorLabel;

    @FXML
    private void sendCode() {
        boolean empty = identifierField.getText().isBlank();
        Ui.error(identifierField, empty);
        Ui.show(codeBox, !empty);
    }

    @FXML
    private void verifyCode() {
        // TODO: no SMS or real password reset yet; this only checks the demo code
        boolean ok = "123456".equals(codeField.getText().trim());
        Ui.error(codeField, !ok);
        Ui.show(errorLabel, !ok);
        if (ok) {
            App.setRoot("login");
        }
    }

    @FXML
    private void backToLogin() {
        App.setRoot("login");
    }
}
