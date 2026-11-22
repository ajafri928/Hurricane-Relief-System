package com.relief_system;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class GuestHelpController {
    @FXML
    private Label errorLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;

    @FXML
    private void continueToRequest() {
        boolean noName = nameField.getText().isBlank();
        boolean noPhone = phoneField.getText().isBlank();
        Ui.error(nameField, noName);
        Ui.error(phoneField, noPhone);
        Ui.show(errorLabel, noName || noPhone);
        if (noName || noPhone) {
            errorLabel.setText("Please enter your name and phone number.");
            return;
        }
        // TODO: guests can't be saved yet, so submitting a request needs backend support
        App.setRoot("request-help");
    }

    @FXML
    private void back() {
        App.setRoot("welcome");
    }
}
