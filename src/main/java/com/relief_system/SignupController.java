package com.relief_system;

import com.model.Location;
import com.model.ReliefSystem;
import com.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignupController {
    @FXML
    private Label errorLabel;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField stateField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void createAccount() {
        boolean missing = false;
        for (TextField field : new TextField[] { firstNameField, lastNameField, usernameField, phoneField,
                addressField, cityField, stateField, passwordField }) {
            boolean empty = field.getText().isBlank();
            Ui.error(field, empty);
            missing |= empty;
        }
        if (missing) {
            showError("Please fill in every field.");
            return;
        }

        String state = stateField.getText().trim();
        if (!state.matches("[A-Za-z]{2}")) {
            Ui.error(stateField, true);
            showError("State must be 2 letters, like SC.");
            return;
        }
        if (passwordField.getText().length() < 8) {
            Ui.error(passwordField, true);
            showError("Your password must be at least 8 characters.");
            return;
        }

        ReliefSystem facade = ReliefSystem.getInstance();
        String username = usernameField.getText().trim();
        Location home = new Location(addressField.getText().trim(), cityField.getText().trim(), state.toUpperCase(), "");
        User user = facade.registerUser(username, firstNameField.getText().trim(), lastNameField.getText().trim(),
                passwordField.getText(), home);
        if (user == null) {
            Ui.error(usernameField, true);
            showError("That username is already taken.");
            return;
        }
        user.setContactPhone(phoneField.getText().trim());
        facade.signIn(username, passwordField.getText());
        App.setRoot("home");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        Ui.show(errorLabel, true);
    }

    @FXML
    private void back() {
        App.setRoot("welcome");
    }
}
