package com.controllers;

import java.io.IOException;

import com.relief_system.App;

import javafx.fxml.FXML;

public class NewController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("create");
    }
}
