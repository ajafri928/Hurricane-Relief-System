package com.relief_system;

import java.util.LinkedHashMap;
import java.util.Map;

import com.model.EmergencyContact;
import com.model.Location;
import com.model.ReliefSystem;
import com.model.SafetyCategory;
import com.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ProfileController {
    private final ReliefSystem facade = ReliefSystem.getInstance();
    private final Map<Button, SafetyCategory> safetyButtons = new LinkedHashMap<>();

    @FXML
    private NavController navController;
    @FXML
    private Label initialsLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Button safeButton;
    @FXML
    private Button cautionButton;
    @FXML
    private Button highRiskButton;
    @FXML
    private Button dangerButton;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private VBox contactList;
    @FXML
    private Label noContactsLabel;

    @FXML
    private void initialize() {
        navController.setActive(NavController.PROFILE);
        User user = facade.getCurrentUser();
        if (user == null) {
            App.setRoot("welcome");
            return;
        }

        String first = user.getFirstName() == null || user.getFirstName().isEmpty() ? "" : user.getFirstName().substring(0, 1);
        String last = user.getLastName() == null || user.getLastName().isEmpty() ? "" : user.getLastName().substring(0, 1);
        initialsLabel.setText((first + last).toUpperCase());
        nameLabel.setText(RequestFormat.fullName(user));
        usernameLabel.setText("@" + user.getUserName());

        safetyButtons.put(safeButton, SafetyCategory.SAFE);
        safetyButtons.put(cautionButton, SafetyCategory.CAUTION);
        safetyButtons.put(highRiskButton, SafetyCategory.HIGH_RISK);
        safetyButtons.put(dangerButton, SafetyCategory.IN_IMMEDIATE_DANGER);
        highlight(user.getSafety());

        String phone = user.getContactPhone();
        phoneLabel.setText(phone == null || phone.isBlank() ? "Not available" : phone);
        Location home = user.getLocation();
        addressLabel.setText(home == null ? "Not available" : home.formatAddress());

        for (EmergencyContact contact : user.getEmergencyContacts()) {
            String relationship = contact.getRelationship() == null || contact.getRelationship().isBlank() ? ""
                    : " · " + contact.getRelationship();
            VBox row = new VBox(2, Ui.label(contact.getName() + relationship, "field-heading"),
                    Ui.label(contact.getPhone(), "hint"));
            contactList.getChildren().add(row);
        }
        Ui.show(noContactsLabel, user.getEmergencyContacts().isEmpty());
    }

    private void highlight(SafetyCategory current) {
        for (Map.Entry<Button, SafetyCategory> entry : safetyButtons.entrySet()) {
            entry.getKey().getStyleClass().remove("selected");
            if (entry.getValue() == current) {
                entry.getKey().getStyleClass().add("selected");
            }
        }
    }

    @FXML
    private void pickSafety(ActionEvent event) {
        SafetyCategory category = safetyButtons.get((Button) event.getSource());
        facade.markUserSafe(category);
        highlight(category);
    }

    @FXML
    private void editProfile() {
        // TODO: edit profile screen
    }

    @FXML
    private void addContact() {
        // TODO: add emergency contact screen
    }

    @FXML
    private void signOut() {
        facade.signOut();
        App.setRoot("welcome");
    }
}
