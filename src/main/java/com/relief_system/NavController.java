package com.relief_system;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class NavController {
    public static final String HOME = "home";
    public static final String MY_REQUESTS = "my-requests";
    public static final String GET_HELP = "request-help";
    public static final String ALERTS = "alerts";
    public static final String PROFILE = "profile";

    @FXML
    private VBox homeTab;
    @FXML
    private VBox requestsTab;
    @FXML
    private VBox helpTab;
    @FXML
    private VBox alertsTab;
    @FXML
    private VBox profileTab;

    public void setActive(String tab) {
        for (VBox t : new VBox[] { homeTab, requestsTab, helpTab, alertsTab, profileTab }) {
            t.getStyleClass().removeAll("nav-active", "nav-active-help");
        }
        switch (tab) {
            case HOME -> homeTab.getStyleClass().add("nav-active");
            case MY_REQUESTS -> requestsTab.getStyleClass().add("nav-active");
            case GET_HELP -> helpTab.getStyleClass().add("nav-active-help");
            case ALERTS -> alertsTab.getStyleClass().add("nav-active");
            case PROFILE -> profileTab.getStyleClass().add("nav-active");
            default -> { }
        }
    }

    @FXML
    private void goHome() {
        App.setRoot(HOME);
    }

    @FXML
    private void goMyRequests() {
        App.setRoot(MY_REQUESTS);
    }

    @FXML
    private void goGetHelp() {
        App.setRoot(GET_HELP);
    }

    @FXML
    private void goAlerts() {
        App.setRoot(ALERTS);
    }

    @FXML
    private void goProfile() {
        App.setRoot(PROFILE);
    }
} 
